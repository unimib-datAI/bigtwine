package it.unimib.disco.bigtwine.services.geo.service;

import it.unimib.disco.bigtwine.commons.messaging.GeoDecoderRequestMessage;
import it.unimib.disco.bigtwine.commons.messaging.GeoDecoderResponseMessage;
import it.unimib.disco.bigtwine.commons.messaging.RequestCounter;
import it.unimib.disco.bigtwine.commons.messaging.ResponseMessage;
import it.unimib.disco.bigtwine.commons.messaging.dto.DecodedLocationDTO;
import it.unimib.disco.bigtwine.services.geo.domain.DecodedLocation;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.geo.decoder.Decoder;
import it.unimib.disco.bigtwine.services.geo.decoder.processors.Processor;
import it.unimib.disco.bigtwine.services.geo.decoder.processors.ProcessorFactory;
import it.unimib.disco.bigtwine.services.geo.domain.Location;
import it.unimib.disco.bigtwine.services.geo.domain.mapper.GeoMapper;
import it.unimib.disco.bigtwine.services.geo.messaging.GeoDecoderRequestsConsumerChannel;
import it.unimib.disco.bigtwine.services.geo.messaging.GeoDecoderResponsesProducerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class GeoService implements ProcessorListener<DecodedLocation> {

    private final Logger log = LoggerFactory.getLogger(GeoService.class);
    private MessageChannel channel;
    private ProcessorFactory processorFactory;
    private KafkaTemplate<Integer, String> kafka;
    private Map<Decoder, Processor> processors = new HashMap<>();
    private Map<String, RequestCounter<GeoDecoderRequestMessage>> requests = new HashMap<>();

    public GeoService(
        GeoDecoderResponsesProducerChannel channel,
        ProcessorFactory processorFactory,
        KafkaTemplate<Integer, String> kafka) {
        this.channel = channel.geoDecoderResponsesChannel();
        this.processorFactory = processorFactory;
        this.kafka = kafka;
    }

    private Decoder getDecoder(String decoderId) {
        if (decoderId != null) {
            decoderId = decoderId.trim();
            if (decoderId.equals("default")) {
                return Decoder.getDefault();
            }else {
                try {
                    return Decoder.valueOf(decoderId);
                }catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }else {
            return Decoder.getDefault();
        }
    }

    private Processor getProcessor(Decoder decoder) {
        Processor processor;
        if (this.processors.containsKey(decoder)) {
            processor = this.processors.get(decoder);
        }else {
            try {
                processor = this.processorFactory.getProcessor(decoder);
            } catch (Exception e) {
                System.err.println("Cannot create processor");
                log.error("Cannot create processor");
                return null;
            }
            processor.setListener(this);
            boolean processorReady = processor.configureProcessor();
            if (processorReady) {
                this.processors.put(decoder, processor);
            }else {
                System.err.println("Processor not ready: " + processor.getDecoder().toString());
                log.error("Processor not ready: " + processor.getDecoder().toString());
                return null;
            }
        }

        log.info("Processor ready: " + processor.getClass().toString());

        return processor;
    }

    private String getNewRequestTag() {
        return UUID.randomUUID().toString();
    }

    private void processDecodeRequest(GeoDecoderRequestMessage request) {
        Decoder decoder = this.getDecoder(request.getDecoder());

        if (decoder == null) {
            this.sendRejected(request);
            return;
        }

        Processor processor = this.getProcessor(decoder);

        if (processor == null) {
            this.sendRejected(request);
            return;
        }

        String tag = this.getNewRequestTag();
        Location[] locations = GeoMapper.INSTANCE.locationsFromDTOs(request.getLocations());
        this.requests.put(tag, new RequestCounter<>(request, locations.length));
        processor.process(tag, locations);
    }

    private void sendExpired(GeoDecoderRequestMessage request) {
        GeoDecoderResponseMessage response = new GeoDecoderResponseMessage();
        response.setStatus(ResponseMessage.Status.EXPIRED);
        this.doSendResponse(request, response);
    }

    private void sendRejected(GeoDecoderRequestMessage request) {
        GeoDecoderResponseMessage response = new GeoDecoderResponseMessage();
        response.setStatus(ResponseMessage.Status.REJECTED);
        this.doSendResponse(request, response);
    }

    private void sendResponse(Processor processor, String tag, DecodedLocation[] addresses) {
        if (!this.requests.containsKey(tag)) {
            log.debug("Request tagged '" + tag + "' expired");
            return;
        }

        RequestCounter<GeoDecoderRequestMessage> requestCounter = this.requests.get(tag);
        requestCounter.decrement(addresses.length);
        GeoDecoderRequestMessage request = requestCounter.get();
        if (!requestCounter.hasMore()) {
            this.requests.remove(tag);
        }

        DecodedLocationDTO[] locationDTOs = GeoMapper.INSTANCE.dtosFromDecodedLocations(addresses);

        GeoDecoderResponseMessage response = new GeoDecoderResponseMessage();
        response.setDecoder(processor.getDecoder().toString());
        response.setLocations(locationDTOs);
        response.setRequestId(request.getRequestId());

        this.doSendResponse(request, response);

        log.info("Request Processed: {}.", tag);
    }

    private void doSendResponse(GeoDecoderRequestMessage request, GeoDecoderResponseMessage response) {
        if (response.getRequestId() == null) {
            response.setRequestId(request.getRequestId());
        }

        if (response.getLocations() == null) {
            response.setLocations(new DecodedLocationDTO[0]);
        }

        MessageBuilder<GeoDecoderResponseMessage> messageBuilder = MessageBuilder
            .withPayload(response);

        if (request.getOutputTopic() != null) {
            messageBuilder.setHeader(KafkaHeaders.TOPIC, request.getOutputTopic());
            this.kafka.send(messageBuilder.build());
        }else {
            this.channel.send(messageBuilder.build());
        }
    }

    @StreamListener(GeoDecoderRequestsConsumerChannel.CHANNEL)
    public void onNewDecodeRequest(GeoDecoderRequestMessage request) {
        if (request.getExpiration() > 0 && System.currentTimeMillis() > request.getExpiration()) {
            log.warn("Request expired before processing: {}.", request.getRequestId());
            this.sendExpired(request);
        } else {
            log.info("Request Received: {}.", request.getRequestId());
            this.processDecodeRequest(request);
        }
    }

    @Override
    public void onProcessed(GenericProcessor processor, String tag, DecodedLocation[] processedItems) {
        if (!(processor instanceof Processor)) {
            throw new AssertionError("Invalid processor type");
        }

        this.sendResponse((Processor)processor, tag, processedItems);
    }
}

package it.unimib.disco.bigtwine.services.nel.service;

import it.unimib.disco.bigtwine.commons.messaging.NelRequestMessage;
import it.unimib.disco.bigtwine.commons.messaging.NelResponseMessage;
import it.unimib.disco.bigtwine.commons.messaging.RequestCounter;
import it.unimib.disco.bigtwine.commons.messaging.ResponseMessage;
import it.unimib.disco.bigtwine.commons.messaging.dto.LinkedTextDTO;
import it.unimib.disco.bigtwine.services.nel.domain.LinkedText;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.nel.domain.RecognizedText;
import it.unimib.disco.bigtwine.services.nel.domain.mapper.NelMapper;
import it.unimib.disco.bigtwine.services.nel.messaging.NelRequestsConsumerChannel;
import it.unimib.disco.bigtwine.services.nel.messaging.NelResponsesProducerChannel;
import it.unimib.disco.bigtwine.services.nel.Linker;
import it.unimib.disco.bigtwine.services.nel.processors.Processor;
import it.unimib.disco.bigtwine.services.nel.processors.ProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class NelService implements ProcessorListener<LinkedText> {

    private final Logger log = LoggerFactory.getLogger(NelService.class);

    private MessageChannel channel;
    private ProcessorFactory processorFactory;
    private KafkaTemplate<Integer, String> kafka;
    private Map<Linker, Processor> processors = new HashMap<>();
    private Map<String, RequestCounter<NelRequestMessage>> requests = new HashMap<>();

    public NelService(
        NelResponsesProducerChannel channel,
        ProcessorFactory processorFactory,
        KafkaTemplate<Integer, String> kafka) {
        this.channel = channel.nelResponsesChannel();
        this.processorFactory = processorFactory;
        this.kafka = kafka;
    }

    private Linker getLinker(String linkerId) {
        if (linkerId != null) {
            linkerId = linkerId.trim();
            if (linkerId.equals("default")) {
                return Linker.getDefault();
            }else {
                try {
                    return Linker.valueOf(linkerId);
                }catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }else {
            return Linker.getDefault();
        }
    }

    private Processor getProcessor(Linker linker) {
        Processor processor;
        if (this.processors.containsKey(linker)) {
            processor = this.processors.get(linker);
        }else {
            try {
                processor = this.processorFactory.getProcessor(linker);
            } catch (Exception e) {
                System.err.println("Cannot create processor");
                log.error("Cannot create processor");
                return null;
            }
            processor.setListener(this);
            boolean processorReady = processor.configureProcessor();
            if (processorReady) {
                this.processors.put(linker, processor);
            }else {
                System.err.println("Processor not ready: " + processor.getLinker().toString());
                log.error("Processor not ready: " + processor.getLinker().toString());
                return null;
            }
        }

        log.info("Processor ready: " + processor.getClass().toString());

        return processor;
    }

    private String getNewRequestTag() {
        return UUID.randomUUID().toString();
    }

    private void processRequest(NelRequestMessage request) {
        Linker linker = this.getLinker(request.getLinker());

        if (linker == null) {
            this.sendRejected(request);
            return;
        }

        Processor processor = this.getProcessor(linker);

        if (processor == null) {
            this.sendRejected(request);
            return;
        }

        String tag = this.getNewRequestTag();
        RecognizedText[] texts = NelMapper.INSTANCE.recognizedTextsFromDTOs(request.getTexts());
        this.requests.put(tag, new RequestCounter<>(request, texts.length));
        processor.process(tag, texts);
    }

    private void sendExpired(NelRequestMessage request) {
        NelResponseMessage response = new NelResponseMessage();
        response.setStatus(ResponseMessage.Status.EXPIRED);
        this.doSendResponse(request, response);
    }

    private void sendRejected(NelRequestMessage request) {
        NelResponseMessage response = new NelResponseMessage();
        response.setStatus(ResponseMessage.Status.REJECTED);
        this.doSendResponse(request, response);
    }

    private void sendResponse(Processor processor, String tag, LinkedText[] texts) {
        if (!this.requests.containsKey(tag)) {
            log.debug("Request tagged '" + tag + "' expired");
            return;
        }

        RequestCounter<NelRequestMessage> requestCounter = this.requests.get(tag);
        requestCounter.decrement(texts.length);
        NelRequestMessage request = requestCounter.get();
        if (!requestCounter.hasMore()) {
            this.requests.remove(tag);
        }

        LinkedTextDTO[] textDTOs = NelMapper.INSTANCE.dtosFromLinkedTexts(texts);

        NelResponseMessage response = new NelResponseMessage();
        response.setLinker(processor.getLinker().toString());
        response.setTexts(textDTOs);
        response.setRequestId(tag);

        this.doSendResponse(request, response);

        log.info("Request Processed: {}.", tag);
    }

    private void doSendResponse(NelRequestMessage request, NelResponseMessage response) {
        if (response.getRequestId() == null) {
            response.setRequestId(request.getRequestId());
        }

        if (response.getTexts() == null) {
            response.setTexts(new LinkedTextDTO[0]);
        }

        MessageBuilder<NelResponseMessage> messageBuilder = MessageBuilder
            .withPayload(response);

        if (request.getOutputTopic() != null) {
            messageBuilder.setHeader(KafkaHeaders.TOPIC, request.getOutputTopic());
            this.kafka.send(messageBuilder.build());
        }else {
            this.channel.send(messageBuilder.build());
        }
    }

    @StreamListener(NelRequestsConsumerChannel.CHANNEL)
    public void onNewRequest(NelRequestMessage request) {
        if (request.getExpiration() > 0 && System.currentTimeMillis() > request.getExpiration()) {
            log.warn("Request expired before processing: {}.", request.getRequestId());
            this.sendExpired(request);
        } else {
            log.info("Request Received: {}.", request.getRequestId());
            this.processRequest(request);
        }
    }

    @Override
    public void onProcessed(GenericProcessor processor, String tag, LinkedText[] tweets) {
        if (!(processor instanceof Processor)) {
            throw new AssertionError("Invalid processor type");
        }

        this.sendResponse((Processor)processor, tag, tweets);
    }
}

package it.unimib.disco.bigtwine.services.ner.service;

import it.unimib.disco.bigtwine.commons.messaging.NerRequestMessage;
import it.unimib.disco.bigtwine.commons.messaging.NerResponseMessage;
import it.unimib.disco.bigtwine.commons.messaging.RequestCounter;
import it.unimib.disco.bigtwine.commons.messaging.ResponseMessage;
import it.unimib.disco.bigtwine.commons.messaging.dto.RecognizedTextDTO;
import it.unimib.disco.bigtwine.services.ner.domain.PlainText;
import it.unimib.disco.bigtwine.services.ner.domain.RecognizedText;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.ner.domain.mapper.NerMapper;
import it.unimib.disco.bigtwine.services.ner.messaging.NerRequestsConsumerChannel;
import it.unimib.disco.bigtwine.services.ner.messaging.NerResponsesProducerChannel;
import it.unimib.disco.bigtwine.services.ner.Recognizer;
import it.unimib.disco.bigtwine.services.ner.processors.NerProcessor;
import it.unimib.disco.bigtwine.services.ner.processors.ProcessorFactory;
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
public class NerService implements ProcessorListener<RecognizedText> {

    private final Logger log = LoggerFactory.getLogger(NerService.class);

    private MessageChannel channel;
    private ProcessorFactory processorFactory;
    private KafkaTemplate<Integer, String> kafka;
    private Map<Recognizer, NerProcessor> processors = new HashMap<>();
    private Map<String, RequestCounter<NerRequestMessage>> requests = new HashMap<>();

    public NerService(
        NerResponsesProducerChannel channel,
        ProcessorFactory processorFactory,
        KafkaTemplate<Integer, String> kafka) {
        this.channel = channel.nerResponsesChannel();
        this.processorFactory = processorFactory;
        this.kafka = kafka;
    }

    private Recognizer getRecognizer(String recognizerId) {
        if (recognizerId != null) {
            recognizerId = recognizerId.trim();
            if (recognizerId.equals("default")) {
                return Recognizer.getDefault();
            }else {
                try {
                    return Recognizer.valueOf(recognizerId);
                }catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }else {
            return Recognizer.getDefault();
        }
    }

    private NerProcessor getProcessor(Recognizer recognizer) {
        NerProcessor processor;
        if (this.processors.containsKey(recognizer)) {
            processor = this.processors.get(recognizer);
        }else {
            try {
                processor = this.processorFactory.getProcessor(recognizer);
            } catch (Exception e) {
                System.err.println("Cannot create processor");
                log.error("Cannot create processor");
                return null;
            }
            processor.setListener(this);
            boolean processorReady = processor.configureProcessor();
            if (processorReady) {
                this.processors.put(recognizer, processor);
            }else {
                System.err.println("NerProcessor not ready: " + processor.getRecognizer().toString());
                log.error("NerProcessor not ready: " + processor.getRecognizer().toString());
                return null;
            }
        }

        log.info("NerProcessor ready: " + processor.getClass().toString());

        return processor;
    }

    private String getNewRequestTag() {
        return UUID.randomUUID().toString();
    }

    private void processRequest(NerRequestMessage request) {
        Recognizer recognizer = this.getRecognizer(request.getRecognizer());

        if (recognizer == null) {
            this.sendRejected(request);
            return;
        }

        NerProcessor processor = this.getProcessor(recognizer);

        if (processor == null) {
            this.sendRejected(request);
            return;
        }

        String tag = this.getNewRequestTag();
        PlainText[] texts = NerMapper.INSTANCE.plainTextsFromDTOs(request.getTexts());
        this.requests.put(tag, new RequestCounter<>(request, texts.length));
        processor.process(tag, texts);
    }

    private void sendExpired(NerRequestMessage request) {
        NerResponseMessage response = new NerResponseMessage();
        response.setStatus(ResponseMessage.Status.EXPIRED);
        this.doSendResponse(request, response);
    }

    private void sendRejected(NerRequestMessage request) {
        NerResponseMessage response = new NerResponseMessage();
        response.setStatus(ResponseMessage.Status.REJECTED);
        this.doSendResponse(request, response);
    }

    private void sendResponse(NerProcessor processor, String tag, RecognizedText[] texts) {
        if (!this.requests.containsKey(tag)) {
            log.debug("Request tagged '" + tag + "' expired");
            return;
        }

        RequestCounter<NerRequestMessage> requestCounter = this.requests.get(tag);
        requestCounter.decrement(texts.length);
        NerRequestMessage request = requestCounter.get();
        if (!requestCounter.hasMore()) {
            this.requests.remove(tag);
        }

        RecognizedTextDTO[] textDTOs = NerMapper.INSTANCE.dtosFromRecognizedTexts(texts);

        NerResponseMessage response = new NerResponseMessage();
        response.setRecognizer(processor.getRecognizer().toString());
        response.setTexts(textDTOs);
        response.setRequestId(tag);

        this.doSendResponse(request, response);

        log.info("Request Processed: {}.", tag);
    }

    private void doSendResponse(NerRequestMessage request, NerResponseMessage response) {
        if (response.getRequestId() == null) {
            response.setRequestId(request.getRequestId());
        }

        if (response.getTexts() == null) {
            response.setTexts(new RecognizedTextDTO[0]);
        }

        MessageBuilder<NerResponseMessage> messageBuilder = MessageBuilder
            .withPayload(response);

        if (request.getOutputTopic() != null) {
            messageBuilder.setHeader(KafkaHeaders.TOPIC, request.getOutputTopic());
            this.kafka.send(messageBuilder.build());
        }else {
            this.channel.send(messageBuilder.build());
        }
    }

    @StreamListener(NerRequestsConsumerChannel.CHANNEL)
    public void onNewRequest(NerRequestMessage request) {
        if (request.getExpiration() > 0 && System.currentTimeMillis() > request.getExpiration()) {
            log.warn("Request expired before processing: {}.", request.getRequestId());
            this.sendExpired(request);
        } else {
            log.info("Request Received: {}.", request.getRequestId());
            this.processRequest(request);
        }
    }

    @Override
    public void onProcessed(GenericProcessor processor, String tag, RecognizedText[] tweets) {
        if (!(processor instanceof NerProcessor)) {
            throw new AssertionError("Invalid processor type");
        }

        this.sendResponse((NerProcessor)processor, tag, tweets);
    }
}

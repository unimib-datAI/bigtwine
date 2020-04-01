package it.unimib.disco.bigtwine.services.linkresolver.service;

import it.unimib.disco.bigtwine.commons.messaging.LinkResolverRequestMessage;
import it.unimib.disco.bigtwine.commons.messaging.LinkResolverResponseMessage;
import it.unimib.disco.bigtwine.commons.messaging.RequestCounter;
import it.unimib.disco.bigtwine.commons.messaging.ResponseMessage;
import it.unimib.disco.bigtwine.commons.messaging.dto.ResourceDTO;
import it.unimib.disco.bigtwine.services.linkresolver.domain.ExtraField;
import it.unimib.disco.bigtwine.services.linkresolver.domain.Link;
import it.unimib.disco.bigtwine.services.linkresolver.domain.Resource;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.linkresolver.LinkType;
import it.unimib.disco.bigtwine.services.linkresolver.domain.mapper.LinkMapper;
import it.unimib.disco.bigtwine.services.linkresolver.processors.Processor;
import it.unimib.disco.bigtwine.services.linkresolver.processors.ProcessorFactory;
import it.unimib.disco.bigtwine.services.linkresolver.messaging.LinkResolverRequestsConsumerChannel;
import it.unimib.disco.bigtwine.services.linkresolver.messaging.LinkResolverResponsesProducerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LinkResolverService implements ProcessorListener<Resource> {

    private final Logger log = LoggerFactory.getLogger(LinkResolverService.class);
    private MessageChannel channel;
    private ProcessorFactory processorFactory;
    private KafkaTemplate<Integer, String> kafka;
    private Map<LinkType, Processor> processors = new HashMap<>();
    private Map<String, RequestCounter<LinkResolverRequestMessage>> requests = new HashMap<>();

    public LinkResolverService(
        LinkResolverResponsesProducerChannel channel,
        ProcessorFactory processorFactory,
        KafkaTemplate<Integer, String> kafka) {
        this.channel = channel.linkResolverResponsesChannel();
        this.processorFactory = processorFactory;
        this.kafka = kafka;
    }

    private Processor getProcessor(LinkType linkType) {
        Processor processor;
        if (this.processors.containsKey(linkType)) {
            processor = this.processors.get(linkType);
        }else {
            try {
                processor = this.processorFactory.getProcessor(linkType);
            } catch (Exception e) {
                System.err.println("Cannot create processor");
                log.error("Cannot create processor");
                return null;
            }
            processor.setListener(this);
            boolean processorReady = processor.configureProcessor();
            if (processorReady) {
                this.processors.put(linkType, processor);
            }else {
                System.err.println("Processor not ready: " + linkType.toString());
                log.error("Processor not ready: " + linkType.toString());
                return null;
            }
        }

        log.info("Processor ready: " + processor.getClass().toString());

        return processor;
    }

    private String getNewRequestTag() {
        return UUID.randomUUID().toString();
    }

    private void processResolveRequest(LinkResolverRequestMessage request) {
        Link[] links = LinkMapper.INSTANCE.linksFromDTOs(request.getLinks());
        ExtraField[] extraFields = LinkMapper.INSTANCE.extraFieldsFromDTOs(request.getExtraFields());
        if (links.length > 0) {
            String tag = this.getNewRequestTag();
            int linksCount = 0;
            Map<LinkType, List<Link>> linksByType = new HashMap<>();

            for (Link link : links) {
                String url = link.getUrl();
                LinkType linkType = LinkType.getTypeOfLink(url);

                if (linkType == null) {
                    continue;
                }

                if (!linksByType.containsKey(linkType)) {
                    linksByType.put(linkType, new ArrayList<>());
                }

                linksByType.get(linkType).add(link);
                linksCount++;
            }

            this.requests.put(tag, new RequestCounter<>(request, linksCount));

            for (Map.Entry<LinkType, List<Link>> entry : linksByType.entrySet()) {
                Processor processor = this.getProcessor(entry.getKey());

                if (processor == null) {
                    continue;
                }

                processor.process(tag, entry.getValue().toArray(new Link[0]), extraFields, request.isSkipCache());
            }
        }
    }

    private void sendExpired(LinkResolverRequestMessage request) {
        LinkResolverResponseMessage response = new LinkResolverResponseMessage();
        response.setStatus(ResponseMessage.Status.EXPIRED);
        this.doSendResponse(request, response);
    }

    private void sendRejected(LinkResolverRequestMessage request) {
        LinkResolverResponseMessage response = new LinkResolverResponseMessage();
        response.setStatus(ResponseMessage.Status.REJECTED);
        this.doSendResponse(request, response);
    }

    private void sendResponse(Processor processor, String tag, Resource[] resources) {
        if (!this.requests.containsKey(tag)) {
            log.debug("Request tagged '" + tag + "' expired");
            return;
        }

        RequestCounter<LinkResolverRequestMessage> requestCounter = this.requests.get(tag);
        requestCounter.decrement(resources.length);
        LinkResolverRequestMessage request = requestCounter.get();
        if (!requestCounter.hasMore()) {
            this.requests.remove(tag);
        }

        ResourceDTO[] resourceDTOs = LinkMapper.INSTANCE.dtosFromResources(resources);

        LinkResolverResponseMessage response = new LinkResolverResponseMessage();
        response.setResources(resourceDTOs);
        response.setRequestId(tag);
        response.setStatus(requestCounter.hasMore() ? ResponseMessage.Status.PARTIAL : ResponseMessage.Status.PROCESSED);
        this.doSendResponse(request, response);

        log.info("Request Processed: {}.", tag);
    }

    private void doSendResponse(LinkResolverRequestMessage request, LinkResolverResponseMessage response) {
        if (response.getRequestId() == null) {
            response.setRequestId(request.getRequestId());
        }

        if (response.getResources() == null) {
            response.setResources(new ResourceDTO[0]);
        }

        MessageBuilder<LinkResolverResponseMessage> messageBuilder = MessageBuilder
            .withPayload(response);

        if (request.getOutputTopic() != null) {
            messageBuilder.setHeader(KafkaHeaders.TOPIC, request.getOutputTopic());
            this.kafka.send(messageBuilder.build());
        }else {
            this.channel.send(messageBuilder.build());
        }
    }

    @StreamListener(LinkResolverRequestsConsumerChannel.CHANNEL)
    public void onNewDecodeRequest(LinkResolverRequestMessage request) {
        if (request.getExpiration() > 0 && System.currentTimeMillis() > request.getExpiration()) {
            log.warn("Request expired before processing: {}.", request.getRequestId());
            this.sendExpired(request);
        } else {
            log.info("Request Received: {}.", request.getRequestId());
            this.processResolveRequest(request);
        }
    }

    @Override
    public void onProcessed(GenericProcessor processor, String tag, Resource[] processedItems) {
        if (!(processor instanceof Processor)) {
            throw new AssertionError("Invalid processor type");
        }

        this.sendResponse((Processor)processor, tag, processedItems);
    }
}

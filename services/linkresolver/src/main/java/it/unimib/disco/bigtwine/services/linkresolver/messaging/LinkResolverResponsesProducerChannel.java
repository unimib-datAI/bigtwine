package it.unimib.disco.bigtwine.services.linkresolver.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface LinkResolverResponsesProducerChannel {
    String CHANNEL = "linkResolverResponsesChannel";

    @Output
    MessageChannel linkResolverResponsesChannel();
}

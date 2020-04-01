package it.unimib.disco.bigtwine.services.linkresolver.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface LinkResolverRequestsConsumerChannel {
    String CHANNEL = "linkResolverRequestsChannel";

    @Input
    SubscribableChannel linkResolverRequestsChannel();
}

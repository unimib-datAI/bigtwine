package it.unimib.disco.bigtwine.services.ner.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface NerRequestsConsumerChannel {
    String CHANNEL = "nerRequestsChannel";

    @Input
    SubscribableChannel nerRequestsChannel();
}

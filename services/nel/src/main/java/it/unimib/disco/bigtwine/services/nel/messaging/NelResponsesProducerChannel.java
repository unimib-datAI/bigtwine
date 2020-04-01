package it.unimib.disco.bigtwine.services.nel.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface NelResponsesProducerChannel {
    String CHANNEL = "nelResponsesChannel";

    @Output
    MessageChannel nelResponsesChannel();
}

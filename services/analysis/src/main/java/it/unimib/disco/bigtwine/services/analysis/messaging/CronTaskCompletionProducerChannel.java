package it.unimib.disco.bigtwine.services.analysis.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CronTaskCompletionProducerChannel {
    String CHANNEL = "cronTaskCompletionChannel";

    @Output
    MessageChannel cronTaskCompletionChannel();
}

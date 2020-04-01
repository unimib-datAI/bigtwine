package it.unimib.disco.bigtwine.services.cronscheduler.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CronTaskCompletionConsumerChannel {
    String CHANNEL = "cronTaskCompletionChannel";

    @Input
    SubscribableChannel cronTaskCompletionChannel();
}

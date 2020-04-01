package it.unimib.disco.bigtwine.services.cronscheduler.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CronTaskProducerChannel {
    String CHANNEL = "cronTaskChannel";

    @Output
    MessageChannel cronTaskChannel();
}

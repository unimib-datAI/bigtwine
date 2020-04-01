package it.unimib.disco.bigtwine.services.analysis.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface JobControlEventsProducerChannel {
    String CHANNEL = "jobControlEventsChannel";

    @Output
    MessageChannel jobControlEventsChannel();
}

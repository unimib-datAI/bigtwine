package it.unimib.disco.bigtwine.services.jobsupervisor.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

public interface AnalysisStatusChangedProducerChannel {
    String CHANNEL = "analysisStatusChangedChannel";

    @Output
    SubscribableChannel analysisStatusChangedChannel();
}

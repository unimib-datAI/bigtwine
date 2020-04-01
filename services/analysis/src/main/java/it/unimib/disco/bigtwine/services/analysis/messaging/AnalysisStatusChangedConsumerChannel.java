package it.unimib.disco.bigtwine.services.analysis.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface AnalysisStatusChangedConsumerChannel {
    String CHANNEL = "analysisStatusChangedChannel";

    @Input
    SubscribableChannel analysisStatusChangedChannel();
}

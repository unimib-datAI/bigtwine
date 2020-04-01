package it.unimib.disco.bigtwine.services.analysis.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface AnalysisProgressUpdateConsumerChannel {

    String CHANNEL = "analysisProgressUpdateChannel";

    @Input
    SubscribableChannel analysisProgressUpdateChannel();
}

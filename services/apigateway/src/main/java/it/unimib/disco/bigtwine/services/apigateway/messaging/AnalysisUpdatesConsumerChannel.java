package it.unimib.disco.bigtwine.services.apigateway.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface AnalysisUpdatesConsumerChannel {

    String CHANNEL = "analysisUpdatesChannel";

    @Input
    SubscribableChannel analysisUpdatesChannel();
}

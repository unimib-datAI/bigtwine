package it.unimib.disco.bigtwine.services.jobsupervisor.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

public interface AnalysisStatusChangeRequestConsumerChannel {
    String CHANNEL = "analysisStatusChangeRequestsChannel";

    @Input
    MessageChannel analysisStatusChangeRequestsChannel();
}

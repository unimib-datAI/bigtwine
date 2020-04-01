package it.unimib.disco.bigtwine.services.apigateway.web.websocket;

import it.unimib.disco.bigtwine.services.apigateway.messaging.AnalysisResultsConsumerChannel;
import it.unimib.disco.bigtwine.services.apigateway.messaging.AnalysisUpdatesConsumerChannel;
import it.unimib.disco.bigtwine.services.apigateway.web.websocket.dto.AnalysisDTO;
import it.unimib.disco.bigtwine.services.apigateway.web.websocket.dto.AnalysisResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final SimpMessageSendingOperations messagingTemplate;

    public AnalysisService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @StreamListener(AnalysisUpdatesConsumerChannel.CHANNEL)
    public void onAnalysisUpdateReceived(AnalysisDTO analysis) {
        log.debug("Analysis update received {}", analysis);
        if (analysis.getId() != null) {
            messagingTemplate.convertAndSend("/topic/analysis-changes/" + analysis.getId(), analysis);
        }
    }

    @StreamListener(AnalysisResultsConsumerChannel.CHANNEL)
    public void onAnalysisResultReceived(AnalysisResultDTO result) {
        log.debug("Analysis result received {}", result);
        if (result.getAnalysisId() != null) {
            messagingTemplate.convertAndSend("/topic/analysis-results/" + result.getAnalysisId(), result);
        }
    }
}

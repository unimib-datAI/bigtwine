package it.unimib.disco.bigtwine.services.analysis.service;

import it.unimib.disco.bigtwine.commons.messaging.AnalysisResultProducedEvent;
import it.unimib.disco.bigtwine.commons.messaging.AnalysisStatusChangedEvent;
import it.unimib.disco.bigtwine.commons.messaging.AnalysisProgressUpdateEvent;
import it.unimib.disco.bigtwine.commons.models.JobTypeEnum;
import it.unimib.disco.bigtwine.services.analysis.domain.Analysis;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisResult;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisResultPayload;
import it.unimib.disco.bigtwine.services.analysis.domain.mapper.*;
import it.unimib.disco.bigtwine.services.analysis.messaging.*;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisResultsRepository;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.AnalysisDTO;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.AnalysisResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class ProcessingOutputDispatcher {

    private final Logger log = LoggerFactory.getLogger(ProcessingOutputDispatcher.class);

    private AnalysisService analysisService;
    private AnalysisResultsRepository resultsRepository;
    private MessageChannel analysisResultsForwardChannel;
    private MessageChannel analysisUpdatesChannel;
    private AnalysisResultPayloadMapperLocator payloadMapperLocator;
    private AnalysisResultMapperLocator resultMapperLocator;

    public ProcessingOutputDispatcher(
        AnalysisService analysisService,
        AnalysisResultsRepository resultsRepository,
        AnalysisResultPayloadMapperLocator payloadMapperLocator,
        AnalysisResultMapperLocator resultMapperLocator,
        AnalysisResultsProducerChannel channel,
        AnalysisUpdatesProducerChannel analysisUpdatesProducerChannel) {
        this.analysisService = analysisService;
        this.resultsRepository = resultsRepository;
        this.payloadMapperLocator = payloadMapperLocator;
        this.resultMapperLocator = resultMapperLocator;
        this.analysisResultsForwardChannel = channel.analysisResultsForwardedChannel();
        this.analysisUpdatesChannel = analysisUpdatesProducerChannel.analysisUpdatesChannel();
    }

    private AnalysisResult<?> saveAnalysisResult(AnalysisResultProducedEvent e) {
        Optional<Analysis> analysis = analysisService.findOne(e.getAnalysisId());

        if (analysis.isPresent()) {
            Object payloadDto = e.getPayload();
            if (payloadDto == null) {
                log.debug("Payload missing");
                return null;
            }

            AnalysisResultPayloadMapper mapper = this.payloadMapperLocator
                .getMapper(payloadDto.getClass());

            if (mapper == null) {
                log.debug("Missing mapper for payload type: {}", payloadDto.getClass());
                return null;
            }

            AnalysisResultPayload payload = mapper.map(payloadDto);

            AnalysisResult<?> result = new AnalysisResult<>()
                .analysis(analysis.get())
                .saveDate(Instant.now())
                .processDate(e.getProcessDate())
                .payload(payload);

            return this.resultsRepository.save(result);
        }

        return null;
    }

    private void forwardAnalysisResult(AnalysisResult result) {
        if (result.getPayload() == null) {
            log.debug("Payload missing");
            return;
        }

        AnalysisResultMapper mapper = this.resultMapperLocator
            .getMapper(result.getPayload().getClass());

        if (mapper == null) {
            log.debug("Missing mapper for analysis result payload type: {}", result.getPayload().getClass());
            return;
        }

        AnalysisResultDTO resultDto = mapper.map(result);
        Message<AnalysisResultDTO> message = MessageBuilder
            .withPayload(resultDto)
            .build();

        this.analysisResultsForwardChannel
            .send(message);
    }

    private void forwardUpdatedAnalysis(Analysis analysis) {
        AnalysisDTO analysisDTO = AnalysisMapper.INSTANCE.analysisDtoFromAnalysis(analysis);
        if (analysisDTO != null) {
            Message<AnalysisDTO> message = MessageBuilder
                .withPayload(analysisDTO)
                .build();

            this.analysisUpdatesChannel.send(message);
        }
    }

    @StreamListener(AnalysisResultsConsumerChannel.CHANNEL)
    public void consumeAnalysisResult(AnalysisResultProducedEvent e) {
        AnalysisResult<?> result = this.saveAnalysisResult(e);
        if (result != null) {
            this.forwardAnalysisResult(result);
        }
    }

    @StreamListener(AnalysisStatusChangedConsumerChannel.CHANNEL)
    public void consumeStatusChangedEvent(AnalysisStatusChangedEvent event) {
        log.debug("Consume status changed event {}", event);

        @SuppressWarnings("unchecked")
        Map<Object, Object> user = (Map<Object, Object>) event.getUser();
        Analysis analysis = this.analysisService.saveAnalysisStatusChange(
            event.getAnalysisId(),
            AnalysisStatusMapper.INSTANCE.analysisStatusFromEventEnum(event.getStatus()),
            AnalysisMapper.INSTANCE.userFromUserMap(user),
            event.getMessage());

        if (analysis != null) {
            this.forwardUpdatedAnalysis(analysis);
        }
    }

    @StreamListener(AnalysisProgressUpdateConsumerChannel.CHANNEL)
    public void consumeProgressUpdateEvent(AnalysisProgressUpdateEvent event) {
        Analysis analysis = null;

        if (event.getJobType() == JobTypeEnum.PROCESSING) {
            analysis = this.analysisService.saveAnalysisProgressUpdate(
                event.getAnalysisId(),
                event.getProgress()
            );
        } else if (event.getJobType() == JobTypeEnum.EXPORT) {
            analysis = this.analysisService.saveAnalysisExportProgressUpdate(
                event.getAnalysisId(),
                event.getReference(),
                event.getProgress(),
                event.isCompleted(),
                event.isFailed(),
                event.getMessage()
            );
        }

        if (analysis != null) {
            this.forwardUpdatedAnalysis(analysis);
        }
    }
}

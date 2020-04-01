package it.unimib.disco.bigtwine.services.analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimib.disco.bigtwine.commons.messaging.AnalysisStatusChangeRequestedEvent;
import it.unimib.disco.bigtwine.commons.messaging.AnalysisStatusChangedEvent;
import it.unimib.disco.bigtwine.commons.models.AnalysisStatusEnum;
import it.unimib.disco.bigtwine.services.analysis.AnalysisApp;
import it.unimib.disco.bigtwine.services.analysis.domain.*;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisVisibility;
import it.unimib.disco.bigtwine.services.analysis.messaging.AnalysisStatusChangeRequestProducerChannel;
import it.unimib.disco.bigtwine.services.analysis.messaging.AnalysisStatusChangedConsumerChannel;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AnalysisApp.class)
public class AnalysisServiceTest {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private MessageCollector messageCollector;

    @Autowired
    private AnalysisStatusChangeRequestProducerChannel statusChangeRequestsChannel;

    @Autowired
    private AnalysisStatusChangedConsumerChannel statusChangedChannel;

    @SpyBean
    private AnalysisService analysisService;

    @Autowired
    private AnalysisRepository analysisRepository;

    private Analysis createAnalysis() {
        AnalysisInput input = new QueryAnalysisInput()
            .tokens(Arrays.asList("query", "di", "prova"))
            .joinOperator(QueryAnalysisInput.JoinOperator.ALL);

        return new Analysis()
            .type(AnalysisType.TWITTER_NEEL)
            .createDate(Instant.now())
            .updateDate(Instant.now())
            .visibility(AnalysisVisibility.PUBLIC)
            .status(AnalysisStatus.READY)
            .owner(new User()
                .uid("testuser-1")
                .username("testuser-1"))
            .input(input);
    }

    @Before
    public void cleanMessageQueue() {
        messageCollector
            .forChannel(statusChangeRequestsChannel.analysisStatusChangeRequestsChannel())
            .clear();
    }

    @Test
    public void requestStatusChange() throws Exception {
        Analysis analysis = this.createAnalysis()
            .status(AnalysisStatus.READY);
        analysis = this.analysisRepository.save(analysis);

        this.analysisService.requestStatusChange(analysis, AnalysisStatus.STARTED, false);

        Message<?> received = messageCollector
            .forChannel(statusChangeRequestsChannel.analysisStatusChangeRequestsChannel())
            .poll();
        assertNotNull(received);
        AnalysisStatusChangeRequestedEvent event = new ObjectMapper().readValue((String)received.getPayload(),
            AnalysisStatusChangeRequestedEvent.class);

        assertThat(event.getAnalysisId()).isEqualTo(analysis.getId());
        assertThat(event.getUser()).isEqualTo(null);
        assertThat(event.getDesiredStatus())
            .isEqualTo(AnalysisStatusEnum.STARTED);
    }

    @Test
    public void consumeStatusChangedEvent() {
        Analysis analysis = this.createAnalysis()
            .status(AnalysisStatus.READY);
        analysis = this.analysisRepository.save(analysis);

        AnalysisStatusChangedEvent event = new AnalysisStatusChangedEvent();
        event.setAnalysisId(analysis.getId());
        event.setStatus(AnalysisStatusEnum.STARTED);

        statusChangedChannel.analysisStatusChangedChannel()
            .send(MessageBuilder.withPayload(event).build());

        verify(this.analysisService, times(1)).saveAnalysisStatusChange(
            analysis.getId(), AnalysisStatus.STARTED, null, null);

        List<AnalysisStatusHistory> statusHistory = this.analysisService
            .getStatusHistory(analysis.getId());

        assertThat(statusHistory).size().isGreaterThan(0);
        assertThat(statusHistory.get(0).getOldStatus()).isEqualTo(AnalysisStatus.READY);
        assertThat(statusHistory.get(0).getNewStatus()).isEqualTo(AnalysisStatus.STARTED);
    }
}

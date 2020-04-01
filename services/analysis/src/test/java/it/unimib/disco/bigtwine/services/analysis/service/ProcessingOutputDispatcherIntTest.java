package it.unimib.disco.bigtwine.services.analysis.service;

import it.unimib.disco.bigtwine.commons.messaging.AnalysisResultProducedEvent;
import it.unimib.disco.bigtwine.commons.messaging.dto.LinkedEntityDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.NeelProcessedTweetDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.TwitterStatusDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.TwitterUserDTO;
import it.unimib.disco.bigtwine.services.analysis.AnalysisApp;
import it.unimib.disco.bigtwine.services.analysis.domain.Analysis;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisResult;
import it.unimib.disco.bigtwine.services.analysis.domain.DatasetAnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.User;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.messaging.AnalysisResultsConsumerChannel;
import it.unimib.disco.bigtwine.services.analysis.messaging.AnalysisResultsProducerChannel;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisResultsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Test class for the LogsResource REST controller.
 *
 * @see ProcessingOutputDispatcher
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AnalysisApp.class)
public class ProcessingOutputDispatcherIntTest {

    @Autowired
    private MessageCollector messageCollector;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private AnalysisResultsRepository resultsRepository;

    @Autowired
    private AnalysisResultsProducerChannel outputChannel;

    @Autowired
    private AnalysisResultsConsumerChannel inputChannel;

    @SpyBean
    private ProcessingOutputDispatcher dispatcher;

    private Analysis analysis;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void setUp() {
        this.analysis = this.analysisService.save(
            new Analysis()
                .type(AnalysisType.TWITTER_NEEL)
                .input(new DatasetAnalysisInput().documentId("testdoc-1"))
                .owner(new User()
                    .uid("testuser-1")
                    .username("testuser-1"))
        );
    }

    @Test
    public void testDispatch() {
        TwitterUserDTO user = new TwitterUserDTO();
        user.setId("testuser-1");
        user.setScreenName("testuser-1");

        TwitterStatusDTO status = new TwitterStatusDTO();
        status.setId("teststatus-1");
        status.setText("text");
        status.setUser(user);

        NeelProcessedTweetDTO tweetPayload = new NeelProcessedTweetDTO();
        tweetPayload.setStatus(status);
        tweetPayload.setEntities(new LinkedEntityDTO[0]);

        AnalysisResultProducedEvent e = new AnalysisResultProducedEvent();
        e.setAnalysisId(this.analysis.getId());
        e.setProcessDate(Instant.now());
        e.setPayload(tweetPayload);

        int tweetsSizeBeforeCreate = resultsRepository.findAll().size();
        Message<AnalysisResultProducedEvent> message = MessageBuilder.withPayload(e).build();
        inputChannel
            .analysisResultsChannel()
            .send(message);

        List<AnalysisResult<?>> tweets = resultsRepository.findAll();
        assertThat(tweets).hasSize(tweetsSizeBeforeCreate + 1);

        Message<?> received = messageCollector
            .forChannel(outputChannel.analysisResultsForwardedChannel())
            .poll();

        assertNotNull(received);
        verify(this.dispatcher, times(1)).consumeAnalysisResult(any());
    }
}

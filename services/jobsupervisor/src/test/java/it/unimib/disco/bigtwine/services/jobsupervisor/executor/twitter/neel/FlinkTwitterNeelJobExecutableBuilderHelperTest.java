package it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel;

import it.unimib.disco.bigtwine.services.jobsupervisor.client.SocialsServiceClient;
import it.unimib.disco.bigtwine.services.jobsupervisor.config.ApplicationDefaults;
import it.unimib.disco.bigtwine.services.jobsupervisor.config.ApplicationProperties;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.OAuthCredentials;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FlinkTwitterNeelJobExecutableBuilderHelperTest {

    @Mock
    private SocialsServiceClient socialsServiceClient;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        OAuthCredentials credentials = new OAuthCredentials();
        credentials.setConsumerKey("123456");
        credentials.setConsumerSecret("78910");
        credentials.setAccessToken("aabbccdd");
        credentials.setAccessTokenSecret("eeffgghh");
        when(socialsServiceClient.findTwitterOAuthCredentials("testuser-1")).thenReturn(credentials);
    }

    @Test
    public void testBuildExecutableCommand() throws Exception {
        FlinkTwitterNeelJobExecutableBuilderHelper helper = this.createHelper();
        List<String> command = helper.buildExecutableCommand(this.createJob());

        assertEquals(4, command.size());
        assertEquals("java", command.get(0));
        assertEquals("-cp", command.get(1));
        assertEquals("StreamProcessor.jar", command.get(2));
        assertEquals("it.unimib.disco.bigtwine.streamprocessor.TwitterStreamJob", command.get(3));
    }

    @Test
    public void testBuildExecutableArgs() throws Exception {
        FlinkTwitterNeelJobExecutableBuilderHelper helper = this.createHelper();
        List<String> args = helper.buildExecutableArgs(this.createJob());

        assertFalse(args.isEmpty());
        verify(socialsServiceClient, times(1)).findTwitterOAuthCredentials("testuser-1");

        Map<String, String> argsMap = this.buildArgsMap(args);
        assertEquals("testjob-1", argsMap.get("--job-id"));
        assertEquals("testanalysis-1", argsMap.get("--analysis-id"));
        assertEquals("aabbccdd", argsMap.get("--twitter-token"));
        assertEquals("eeffgghh", argsMap.get("--twitter-token-secret"));
        assertEquals("123456", argsMap.get("--twitter-consumer-key"));
        assertEquals("78910", argsMap.get("--twitter-consumer-secret"));
        assertEquals("'testquery'", argsMap.get("--twitter-stream-query"));
        assertEquals("en", argsMap.get("--twitter-stream-lang"));
        assertEquals("-1", argsMap.get("--twitter-stream-sampling"));
        assertEquals("30", argsMap.get("--heartbeat-interval"));
    }

    private FlinkTwitterNeelJobExecutableBuilderHelper createHelper() {
        ApplicationProperties props = new ApplicationProperties();

        return new FlinkTwitterNeelJobExecutableBuilderHelper(props, socialsServiceClient);
    }

    private Job createJob() {
        Map<String, Object> input = new HashMap<>();
        input.put(AnalysisInfo.InputKeys.TYPE, "query");
        input.put(AnalysisInfo.InputKeys.TOKENS, Arrays.asList("test", "query"));
        input.put(AnalysisInfo.InputKeys.JOIN_OPERATOR, "all");
        UserInfo owner = new UserInfo();
        owner.setUid("testuser-1");
        owner.setUsername("testuser-1");

        AnalysisInfo analysis = new AnalysisInfo();
        analysis.setId("testanalysis-1");
        analysis.setType("TWITTER_NEEL");
        analysis.setInput(input);
        analysis.setOwner(owner);

        Job job = new Job();
        job.setId("testjob-1");
        job.setAnalysis(analysis);

        return job;
    }

    private Map<String, String> buildArgsMap(List<String> args) {
        Map<String, String> argsMap = new HashMap<>();
        for (int i = 0; i < args.size(); i += 2) {
            argsMap.put(args.get(i), args.get(i+1));
        }
        return argsMap;
    }
}

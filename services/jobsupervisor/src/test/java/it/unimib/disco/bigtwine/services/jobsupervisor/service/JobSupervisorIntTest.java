package it.unimib.disco.bigtwine.services.jobsupervisor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimib.disco.bigtwine.commons.messaging.AnalysisStatusChangeRequestedEvent;
import it.unimib.disco.bigtwine.commons.messaging.AnalysisStatusChangedEvent;
import it.unimib.disco.bigtwine.commons.messaging.JobHeartbeatEvent;
import it.unimib.disco.bigtwine.commons.models.AnalysisStatusEnum;
import it.unimib.disco.bigtwine.services.jobsupervisor.JobsupervisorApp;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.UserInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.*;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.shell.ShellJobProcess;
import it.unimib.disco.bigtwine.services.jobsupervisor.messaging.AnalysisStatusChangeRequestConsumerChannel;
import it.unimib.disco.bigtwine.services.jobsupervisor.messaging.AnalysisStatusChangedProducerChannel;
import it.unimib.disco.bigtwine.services.jobsupervisor.messaging.JobHeartbeatConsumerChannel;
import it.unimib.disco.bigtwine.services.jobsupervisor.repository.JobRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobsupervisorApp.class)
public class JobSupervisorIntTest {
    @Autowired
    private MessageCollector messageCollector;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobHeartbeatConsumerChannel heartbeatConsumerChannel;

    @Autowired
    private AnalysisStatusChangeRequestConsumerChannel statusChangeRequestConsumerChannel;

    @Autowired
    private AnalysisStatusChangedProducerChannel statusChangedProducerChannel;

    @Mock
    private JobExecutableBuilderLocator jobExecutableBuilderLocator;

    @Mock
    private JobExecutableBuilder jobExecutableBuilder;

    @Mock
    private JobExecutor<JobProcess, JobExecutable> jobExecutor;

    @SpyBean
    private JobService jobService;

    @SpyBean
    private JobSupervisor jobSupervisor;

    @Before
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(jobExecutor.test(any(JobProcess.class))).thenReturn(true);
        when(jobExecutor.test(any(JobExecutable.class))).thenReturn(true);
        when(jobExecutor.stop(any(JobProcess.class))).thenReturn(true);

        when(jobExecutableBuilderLocator.getJobExecutableBuilder(any(Job.class))).thenReturn(jobExecutableBuilder);
        when(jobExecutableBuilder.build()).thenReturn(mock(JobExecutable.class));

        ReflectionTestUtils.setField(jobSupervisor, "jobService", jobService);
        ReflectionTestUtils.setField(jobSupervisor, "jobExecutor", jobExecutor);
        ReflectionTestUtils.setField(jobSupervisor, "jobExecutableBuilderLocator", jobExecutableBuilderLocator);
    }

    @After
    public void clearRepository() {
        this.jobRepository.deleteAll();
    }

    @Test
    public void testStartJob() throws Exception {
        Job job = this.jobRepository.save(this.createJob("testanalysis-2", true));

        doReturn(job).when(jobService).createRunningJobForAnalysis("testanalysis-2");
        when(jobExecutor.execute(any(JobExecutable.class))).thenReturn(new ShellJobProcess("testpid-1"));

        sendChangeStatusRequest("testanalysis-2", AnalysisStatusEnum.STARTED, true);

        Job updatedJob = this.jobRepository.findById(job.getId()).orElse(null);

        assertNotNull(updatedJob);
        assertNotNull(updatedJob.getLastUpdateDate());
        assertNotNull(updatedJob.getProcess());
        assertEquals("testpid-1", updatedJob.getProcess().getPID());
        assertTrue(updatedJob.getProcess() instanceof ShellJobProcess);
        assertNull(updatedJob.getEndDate());
        assertTrue(updatedJob.isRunning());

        AnalysisStatusChangedEvent resEvent = this.pollStatusChangedEvent();

        assertEquals("testanalysis-2", resEvent.getAnalysisId());
        assertEquals(AnalysisStatusEnum.STARTED, resEvent.getStatus());
    }

    @Test
    public void testStopJob() throws Exception {
        Job j = this.createJob("testanalysis-1", true);
        j.setProcess(new ShellJobProcess("testpid-1"));
        Job job = this.jobRepository.save(j);

        sendChangeStatusRequest("testanalysis-1", AnalysisStatusEnum.STOPPED, false);

        Job updatedJob = this.jobRepository.findById(job.getId()).orElse(null);

        assertNotNull(updatedJob);
        assertNotNull(updatedJob.getLastUpdateDate());
        assertNotNull(updatedJob.getEndDate());
        assertFalse(updatedJob.isRunning());

        AnalysisStatusChangedEvent resEvent = this.pollStatusChangedEvent();

        assertEquals("testanalysis-1", resEvent.getAnalysisId());
        assertEquals(AnalysisStatusEnum.STOPPED, resEvent.getStatus());
        assertNull(resEvent.getUser());
    }

    @Test
    public void testUnstoppableJob() throws Exception {
        when(jobExecutor.stop(any(JobProcess.class))).thenReturn(false);

        Job j = this.createJob("testanalysis-1", true);
        j.setProcess(new ShellJobProcess("testpid-1"));
        this.jobRepository.save(j);

        sendChangeStatusRequest("testanalysis-1", AnalysisStatusEnum.STOPPED, true);

        AnalysisStatusChangedEvent resEvent = this.pollStatusChangedEvent();

        assertEquals("testanalysis-1", resEvent.getAnalysisId());
        assertNull(resEvent.getStatus());
    }

    @Test
    public void testAlreadyStoppedJob() throws Exception {
        final String analysisId = "testanalysis-1";

        Job j = this.createJob(analysisId, false);
        this.jobRepository.save(j);

        sendChangeStatusRequest(analysisId, AnalysisStatusEnum.STOPPED, true);

        AnalysisStatusChangedEvent resEvent = this.pollStatusChangedEvent();

        assertEquals(analysisId, resEvent.getAnalysisId());
        assertNull(resEvent.getStatus());
    }

    private void sendChangeStatusRequest(String analysisId, AnalysisStatusEnum desiredStatus, boolean userRequested) {
        AnalysisStatusChangeRequestedEvent event = new AnalysisStatusChangeRequestedEvent();
        event.setAnalysisId(analysisId);
        event.setUser(null);
        event.setDesiredStatus(desiredStatus);

        this.statusChangeRequestConsumerChannel
            .analysisStatusChangeRequestsChannel()
            .send(MessageBuilder.withPayload(event).build());

        verify(this.jobSupervisor, times(1)).newAnalysisStatusChangeRequest(any(AnalysisStatusChangeRequestedEvent.class));
    }

    @Test
    public void testConsumeHeartbeat() {
        Job job = this.jobRepository.save(this.createJob("testanalysis-1", true));

        Instant ts = Instant.now();
        JobHeartbeatEvent event = new JobHeartbeatEvent();
        event.setJobId(job.getId());
        event.setTimestamp(ts);

        heartbeatConsumerChannel
            .jobHeartbeatsChannel()
            .send(MessageBuilder.withPayload(event).build());

        Job updatedJob = this.jobRepository.findById(job.getId()).orElse(null);

        assertNotNull(updatedJob);
        assertNotNull(updatedJob.getLastUpdateDate());
        assertTrue(ts.isBefore(updatedJob.getLastUpdateDate()));
        assertEquals(ts, updatedJob.getLastHeartbeatDate());

        verify(this.jobSupervisor, times(1)).jobHeartbeatReceived(any(JobHeartbeatEvent.class));
    }

    private AnalysisInfo createAnalysis() {
        Map<String, Object> input = new HashMap<>();
        input.put(AnalysisInfo.InputKeys.TYPE, AnalysisInfo.InputType.QUERY);
        input.put(AnalysisInfo.InputKeys.TOKENS, Collections.singletonList("testquery"));
        input.put(AnalysisInfo.InputKeys.JOIN_OPERATOR, "all");
        UserInfo owner = new UserInfo();
        owner.setUid("testuser-1");
        owner.setUsername("testuser-1");
        AnalysisInfo analysis = new AnalysisInfo();
        analysis.setId("testanalysis-1");
        analysis.setType("TWITTER_NEEL");
        analysis.setInput(input);
        analysis.setOwner(owner);

        return analysis;

    }

    private Job createJob(String analysisId, boolean running) {
        AnalysisInfo analysis = this.createAnalysis();
        analysis.setId(analysisId);

        Job job = new Job();
        job.setAnalysis(analysis);
        job.setRunning(running);
        job.setLastUpdateDate(Instant.now());

        return job;
    }

    private AnalysisStatusChangedEvent pollStatusChangedEvent() throws Exception {
        Message<?> received = messageCollector
            .forChannel(statusChangedProducerChannel.analysisStatusChangedChannel())
            .poll();

        assertNotNull(received);

        AnalysisStatusChangedEvent event = new ObjectMapper()
            .readValue((String)received.getPayload(), AnalysisStatusChangedEvent.class);

        assertNotNull(event);

        return event;
    }
}

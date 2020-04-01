package it.unimib.disco.bigtwine.services.jobsupervisor.service;

import it.unimib.disco.bigtwine.services.jobsupervisor.JobsupervisorApp;
import it.unimib.disco.bigtwine.services.jobsupervisor.client.AnalysisServiceClient;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.UserInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobProcess;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.shell.ShellJobProcess;
import it.unimib.disco.bigtwine.services.jobsupervisor.repository.JobRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobsupervisorApp.class)
public class JobServiceIntTest {

    @Autowired
    private JobRepository jobRepository;

    @Mock
    private AnalysisServiceClient analysisServiceClient;

    private JobService jobService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        when(analysisServiceClient.findAnalysisById(anyString())).thenAnswer((invocation) -> {
            AnalysisInfo analysisInfo = this.createAnalysis();
            analysisInfo.setId(invocation.getArgument(0));

            return analysisInfo;
        });

        this.jobService = new JobService(jobRepository, analysisServiceClient);
    }

    @After
    public void clearRepository() {
        this.jobRepository.deleteAll();
    }

    @Test
    public void testCreateRunningJobForAnalysis() throws Exception {
        Job job = this.jobService.createRunningJobForAnalysis("testanalysis-1");

        assertTrue(job.isRunning());
        assertEquals(job.getAnalysis().getId(), "testanalysis-1");
        assertNull(job.getProcess());
    }

    @Test
    public void testUpdateJobProcess() throws Exception {
        Job job1 = this.jobRepository.save(this.createJob("testanalysis-1", true));
        JobProcess process = new ShellJobProcess("testpid-1");

        this.jobService.updateJobProcess(job1.getId(), process);

        Job updateJob = this.jobRepository.findById(job1.getId()).orElseThrow(Exception::new);

        assertNotNull(updateJob.getProcess());
        assertEquals(updateJob.getProcess().getPID(), "testpid-1");
        assertEquals(updateJob.getProcess().getClass().getSimpleName(), "ShellJobProcess");
        assertTrue(job1.getLastUpdateDate().isBefore(updateJob.getLastUpdateDate()));
    }

    @Test
    public void testFindRunningJobForAnalysis() {
        this.jobRepository.save(this.createJob("testanalysis-1", false));
        Job job2 = this.jobRepository.save(this.createJob("testanalysis-1", true));
        this.jobRepository.save(this.createJob("testanalysis-2", false));
        this.jobRepository.save(this.createJob("testanalysis-3", true));

        Optional<Job> runningJob = this.jobService.findRunningJobForAnalysis("testanalysis-1");

        assertTrue(runningJob.isPresent());
        assertEquals(job2.getId(), runningJob.get().getId());
    }

    @Test(expected = JobService.JobAlreadyRunningExecption.class)
    public void testUniqueRunningJob() throws Exception {
        this.jobService.createRunningJobForAnalysis("testanalysis-1");
        this.jobService.createRunningJobForAnalysis("testanalysis-1");
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
}

package it.unimib.disco.bigtwine.services.jobsupervisor.repository;

import it.unimib.disco.bigtwine.services.jobsupervisor.JobsupervisorApp;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobProcess;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.KubernetesJobProcess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobsupervisorApp.class)
public class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Test
    public void testJobRepo() {
        AnalysisInfo analysisInfo = new AnalysisInfo();
        analysisInfo.setId("456");

        JobProcess jobProcess = new KubernetesJobProcess("batch/v1", "Job", "1");

        Job job = new Job();
        job.setAnalysis(analysisInfo);
        job.setProcess(jobProcess);

        Job savedJob = jobRepository.insert(job);

        Optional<Job> findJob = jobRepository.findById(savedJob.getId());

        System.out.println("Job process: " + findJob.get().getProcess().getClass());
    }

}

package it.unimib.disco.bigtwine.services.jobsupervisor.service;

import it.unimib.disco.bigtwine.services.jobsupervisor.client.AnalysisServiceClient;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.enumeration.JobType;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobProcess;
import it.unimib.disco.bigtwine.services.jobsupervisor.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;


@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final AnalysisServiceClient analysisServiceClient;

    public JobService(JobRepository jobRepository, AnalysisServiceClient analysisServiceClient) {
        this.jobRepository = jobRepository;
        this.analysisServiceClient = analysisServiceClient;
    }

    @Transactional
    public Job createRunningJobForAnalysis(String analysisId, JobType jobType, String reference) throws JobAlreadyRunningExecption {
        AnalysisInfo analysis;

        if (jobType == null) {
            jobType = JobType.DEFAULT;
        }

        try {
            analysis = this.analysisServiceClient.findAnalysisById(analysisId);
        }catch(Exception e) {
            log.error("Analysis {} not found", analysisId, e);
            throw new NoSuchAnalysisException();
        }

        Optional<Job> runningJob = this.findRunningJobForAnalysis(analysisId, jobType);
        if (runningJob.isPresent()) {
            throw new JobAlreadyRunningExecption(runningJob.get());
        }

        Instant now = Instant.now();
        Job job = new Job();
        job.setAnalysis(analysis);
        job.setJobType(jobType);
        job.setReference(reference);
        job.setRunning(true);
        job.setStartDate(now);
        job.setLastUpdateDate(now);

        return this.jobRepository.insert(job);
    }

    @Transactional
    public Job createRunningJobForAnalysis(String analysisId, JobType jobType) throws JobAlreadyRunningExecption {
        return this.createRunningJobForAnalysis(analysisId, jobType, null);
    }

    @Transactional
    public Job createRunningJobForAnalysis(String analysisId) throws JobAlreadyRunningExecption {
        return this.createRunningJobForAnalysis(analysisId, JobType.DEFAULT, null);
    }

    @Transactional
    public Job updateJobProcess(String jobId, JobProcess process) {
        Job job = this.jobRepository.findById(jobId)
            .orElseThrow(NoSuchJobException::new);

        job.setProcess(process);
        job.setLastUpdateDate(Instant.now());

        return this.jobRepository.save(job);
    }

    @Transactional
    public Job endJob(String jobId, String endReason) {
        Job job = this.jobRepository.findById(jobId)
            .orElseThrow(NoSuchJobException::new);

        Instant now = Instant.now();
        job.setRunning(false);
        job.setEndReason(endReason);
        job.setEndDate(now);
        job.setLastUpdateDate(now);

        return this.jobRepository.save(job);
    }

    public Optional<Job> findRunningJobForAnalysis(String analysisId, JobType jobType) {
        return this.jobRepository
            .findRunningJobForAnalysisAndJobType(analysisId, jobType)
            .max(Comparator.comparing(Job::getLastUpdateDate));
    }

    public Optional<Job> findRunningJobForAnalysis(String analysisId) {
        return this.findRunningJobForAnalysis(analysisId, JobType.DEFAULT);
    }

    public Optional<Job> findById(String jobId) {
        return this.jobRepository.findById(jobId);
    }

    public Job saveJobHeartbeat(String jobId, Instant timestamp, double progress, boolean isLast, boolean isFailed, String message) {
        Job job = this.jobRepository.findById(jobId)
            .orElseThrow(NoSuchJobException::new);

        if (job.getLastHeartbeatDate() != null && job.getLastHeartbeatDate().isAfter(timestamp)) {
            return job;
        }

        job.setLastHeartbeatDate(timestamp);
        job.setLastUpdateDate(Instant.now());

        if (progress >= 0) {
            job.setProgress(progress);
        }

        if (isLast || isFailed) {
            job.setRunning(false);
            job.setEndDate(timestamp);

            if (isFailed) {
                job.setEndReason("Job failed with error: " + message);
            } else {
                job.setEndReason("Job completed");
            }
        }

        return this.jobRepository.save(job);
    }

    public class NoSuchAnalysisException extends IllegalArgumentException {

    }

    public class JobAlreadyRunningExecption extends Exception {
        private Job runningJob;

        public JobAlreadyRunningExecption(String message) {
            super(message);
        }

        public JobAlreadyRunningExecption(String message, Job job) {
            super(message);
            this.runningJob = job;
        }

        public JobAlreadyRunningExecption(Job job) {
            this("Job already running, original runningJob id: " + job.getId(), job);
        }

        public Job getRunningJob() {
            return runningJob;
        }
    }

    public class NoSuchJobException extends IllegalArgumentException {

    }
}

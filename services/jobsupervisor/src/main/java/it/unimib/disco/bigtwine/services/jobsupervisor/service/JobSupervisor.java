package it.unimib.disco.bigtwine.services.jobsupervisor.service;

import it.unimib.disco.bigtwine.commons.messaging.*;
import it.unimib.disco.bigtwine.commons.models.AnalysisStatusEnum;
import it.unimib.disco.bigtwine.commons.models.JobTypeEnum;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.enumeration.JobType;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.*;
import it.unimib.disco.bigtwine.services.jobsupervisor.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.messaging.MessageChannel;

import java.time.Instant;

@Service
public class JobSupervisor {

    private static final Logger log = LoggerFactory.getLogger(JobSupervisor.class);

    private final JobService jobService;
    private final MessageChannel statusChangedChannel;
    private final MessageChannel progressUpdatesChannel;
    private final JobExecutor jobExecutor;
    private final JobExecutableBuilderLocator jobExecutableBuilderLocator;

    public JobSupervisor(
        JobService jobService,
        AnalysisStatusChangedProducerChannel statusChannel,
        AnalysisProgressUpdatesProducerChannel progressChannel,
        JobExecutor jobExecutor,
        JobExecutableBuilderLocator jobExecutableBuilderLocator) {
        this.jobService = jobService;
        this.statusChangedChannel = statusChannel.analysisStatusChangedChannel();
        this.progressUpdatesChannel = progressChannel.analysisProgressUpdateChannel();
        this.jobExecutor = jobExecutor;
        this.jobExecutableBuilderLocator = jobExecutableBuilderLocator;
    }

    private Job startAnalysisJob(JobType jobType, String analysisId, String reference) throws JobExecutionException {
        if (jobType == null || analysisId == null) {
            throw new IllegalArgumentException("jobType and analysisId must not be null");
        }

        Job newJob;
        try {
            newJob = this.jobService.createRunningJobForAnalysis(analysisId, jobType, reference);
        } catch (JobService.NoSuchAnalysisException e) {
            throw new JobExecutionException(String.format("Analysis with id %s not found", analysisId), e);
        } catch (JobService.JobAlreadyRunningExecption e){
            throw new JobExecutionException(String.format("A job with type %s for analysis %s is running already (job id %s)",
                jobType, analysisId, e.getRunningJob().getId()), e);
        }

        JobExecutableBuilder builder;
        try {
            builder = this.jobExecutableBuilderLocator.getJobExecutableBuilder(newJob);
        } catch (JobExecutableBuilderLocator.JobExecutableBuilderNotFoundException e) {
            log.debug("Job executable builder not found", e);
            this.jobService.endJob(newJob.getId(), "Job executable builder not found");
            throw new JobExecutionException(String.format("Job executable builder for analysis %s not available",
                analysisId), e);
        }

        JobExecutable executable;
        try {
            builder.setJob(newJob);
            executable = builder.build();
        } catch (JobExecutableBuilder.BuildException e) {
            log.error("Job executable cannot be created", e);
            this.jobService.endJob(newJob.getId(), "Job executable cannot be created: " + e.getLocalizedMessage());
            throw new JobExecutionException(String.format("A job executable for analysis %s cannot be created: %s",
                analysisId, e.getLocalizedMessage()), e);
        }

        if (!this.jobExecutor.test(executable)) {
            throw new AssertionError(String.format("Executable is not valid for current executor, " +
                "invalid configuration. Executable type %s, Executor type: %s",
                executable.getClass(), this.jobExecutor.getClass()));
        }

        JobProcess process = null;
        Throwable cause = null;
        try {
            @SuppressWarnings("unchecked")
            JobProcess p = this.jobExecutor.execute(executable);
            process = p;
        } catch (JobExecutor.JobExecutorException e) {
            log.error("Job executable cannot be executed", e);
            cause = e;
        }

        if (process == null) {
            this.jobService.endJob(newJob.getId(), "Job executable cannot be launched");
            throw new JobExecutionException(String.format("Job executable for analysis %s cannot be launched",
                analysisId), cause);
        }

        return this.jobService.updateJobProcess(newJob.getId(), process);
    }

    private Job startAnalysisJob(JobType jobType, String analysisId) throws JobExecutionException {
        return this.startAnalysisJob(jobType, analysisId, null);
    }

    @SuppressWarnings("UnusedReturnValue")
    private Job startAnalysisJob(String analysisId) throws JobExecutionException {
        return this.startAnalysisJob(JobType.DEFAULT, analysisId);
    }

    private Job stopAnalysisJob(JobType jobType, String analysisId, boolean endJobIfStopFail) throws JobExecutionException {
        Job job = this.jobService
            .findRunningJobForAnalysis(analysisId, jobType)
            .orElseThrow(() -> new JobExecutionException(String.format("Running job with type %s for analysis %s not found", jobType, analysisId)));

        return stopJob(job, endJobIfStopFail);
    }

    @SuppressWarnings("UnusedReturnValue")
    private Job stopAnalysisJob(String analysisId, boolean endJobIfStopFail) throws JobExecutionException {
        return this.stopAnalysisJob(JobType.DEFAULT, analysisId, endJobIfStopFail);
    }

    private Job cancelAnalysisJob(JobType jobType, String analysisId) throws JobExecutionException {
        return this.stopAnalysisJob(jobType, analysisId, true);
    }

    @SuppressWarnings("UnusedReturnValue")
    private Job cancelAnalysisJob(String analysisId) throws JobExecutionException {
        return this.cancelAnalysisJob(JobType.DEFAULT, analysisId);
    }

    private Job stopJob(Job job, boolean endJobIfStopFail) throws JobExecutionException {
        if (job == null || job.getAnalysis() == null || job.getAnalysis().getId() == null) {
            return null;
        }

        String analysisId = job.getAnalysis().getId();
        if (job.getProcess() == null) {
            this.jobService.endJob(job.getId(), "Job process missing");
            throw new JobExecutionException(String.format("Job executable for analysis %s has not a process associated",
                analysisId));
        }

        if (!this.jobExecutor.test(job.getProcess())) {
            throw new AssertionError(String.format("Process is not valid for current executor, " +
                    "invalid configuration. Job type %s, Executor type: %s",
                job.getProcess().getClass(), this.jobExecutor.getClass()));
        }

        boolean stopped = false;
        Throwable cause = null;
        try {
            @SuppressWarnings("unchecked")
            boolean s = this.jobExecutor.stop(job.getProcess());
            stopped = s;
        } catch (JobExecutor.JobExecutorException e) {
            log.error("Cannot stop job", e);
            cause = e;
        }

        if (stopped) {
            job = this.jobService.endJob(job.getId(), "Job successfull stopped");
        }else {
            if (endJobIfStopFail) {
                this.jobService.endJob(job.getId(), "Job cannot be stopped, cancelled");
            }

            throw new JobExecutionException(String.format("Running job executable for analysis %s cannot be stopped",
                analysisId), cause);
        }

        return job;
    }

    private Job stopJob(String jobId, boolean endJobIfStopFail) throws JobExecutionException {
        Job job = this.jobService
            .findById(jobId)
            .orElseThrow(() -> new JobExecutionException(String.format("Job with id %s not found", jobId)));

        return stopJob(job, endJobIfStopFail);
    }

    private void notifyAnalysisStatusChange(String analysisId, AnalysisStatusEnum newStatus, Object user, String failMessage) {
        AnalysisStatusChangedEvent event = new AnalysisStatusChangedEvent();
        event.setAnalysisId(analysisId);
        event.setStatus(newStatus);
        event.setUser(user);
        event.setMessage(failMessage);

        Message<AnalysisStatusChangedEvent> message = MessageBuilder
            .withPayload(event)
            .build();

        this.statusChangedChannel.send(message);
    }

    private void notifyAnalysisProgressUpdate(
        String analysisId,
        JobType jobType,
        String reference,
        double progress,
        boolean isCompleted,
        boolean isFailed,
        Instant timestamp,
        String message) {
        AnalysisProgressUpdateEvent event = new AnalysisProgressUpdateEvent();
        event.setAnalysisId(analysisId);
        event.setJobType(JobTypeEnum.valueOf(jobType.name()));
        event.setReference(reference);
        event.setProgress(progress);
        event.setFailed(isFailed);
        event.setCompleted(isCompleted);
        event.setMessage(message);
        event.setTimestamp(timestamp);

        Message<AnalysisProgressUpdateEvent> msg = MessageBuilder
            .withPayload(event)
            .build();

        this.progressUpdatesChannel.send(msg);
    }

    @StreamListener(AnalysisStatusChangeRequestConsumerChannel.CHANNEL)
    public void newAnalysisStatusChangeRequest(AnalysisStatusChangeRequestedEvent event) {
        String analysisId = event.getAnalysisId();
        Object user = event.getUser();
        AnalysisStatusEnum desiredStatus = event.getDesiredStatus();
        AnalysisStatusEnum newStatus;
        String failMessage = null;

        if (desiredStatus == AnalysisStatusEnum.STARTED) {
            try {
                this.startAnalysisJob(analysisId);
                newStatus = AnalysisStatusEnum.STARTED;
            } catch (JobExecutionException e) {
                if (e.getCause() instanceof JobService.JobAlreadyRunningExecption) {
                    newStatus = null;
                } else {
                    newStatus = AnalysisStatusEnum.FAILED;
                }

                failMessage = e.getMessage();
                e.printStackTrace();
            }
        }else if (desiredStatus == AnalysisStatusEnum.STOPPED ||
            desiredStatus == AnalysisStatusEnum.COMPLETED) {
            try {
                this.stopAnalysisJob(analysisId, false);
                newStatus = desiredStatus;
            }catch (JobExecutionException e) {
                newStatus = null;
                failMessage = e.getMessage();
                e.printStackTrace();
            }
        }else if (desiredStatus == AnalysisStatusEnum.CANCELLED) {
            try {
                this.cancelAnalysisJob(analysisId);
                newStatus = AnalysisStatusEnum.CANCELLED;
            }catch (JobExecutionException e) {
                newStatus = null;
                failMessage = e.getMessage();
                e.printStackTrace();
            }
        }else {
            log.debug("Invalid change status request received {} for analysis: {} (user requested: {})", analysisId, event.getDesiredStatus(), user);
            return;
        }

        this.notifyAnalysisStatusChange(analysisId, newStatus, user, failMessage);
    }

    @StreamListener(JobControlEventsConsumerChannel.CHANNEL)
    public void jobControlEventReceived(JobControlEvent event) {
        log.info("Job control event received: {}, {}, {}, {}",
            event.getJobId(), event.getJobType(), event.getAction(), event.getAnalysisId());

        try {
            switch (event.getAction()) {
                case START:
                    startAnalysisJob(JobType.valueOf(event.getJobType().name()), event.getAnalysisId(), event.getReference());
                    break;
                case STOP:
                    stopJob(event.getJobId(), false);
                    break;
                case CANCEL:
                    stopJob(event.getJobId(), true);
                    break;
                default:
                    log.warn("Unsupported control event received {}", event.getAction());
            }
        } catch (JobExecutionException | IllegalArgumentException e) {
            log.error("Cannot execute job control", e);
        }
    }

    @StreamListener(JobHeartbeatConsumerChannel.CHANNEL)
    public void jobHeartbeatReceived(JobHeartbeatEvent event) {
        if (event.getJobId() == null || event.getTimestamp() == null) {
            return;
        }

        try {
            Job job = this.jobService.saveJobHeartbeat(
                event.getJobId(),
                event.getTimestamp(),
                event.getProgress(),
                event.isLast(),
                event.isFailed(),
                event.getMessage());

            if (job.getProgress() >= 0 || event.isLast() || event.isFailed()) {
                this.notifyAnalysisProgressUpdate(
                    job.getAnalysis().getId(),
                    job.getJobType(),
                    job.getReference(),
                    job.getProgress(),
                    event.isLast(),
                    event.isFailed(),
                    event.getTimestamp(),
                    event.getMessage());
            }

            if (job.getJobType() == JobType.PROCESSING && (event.isLast() || event.isFailed())) {
                AnalysisStatusEnum nextStatus;
                String failMessage = null;
                if (event.isLast()) {
                    stopJob(job, true);
                    nextStatus = AnalysisStatusEnum.COMPLETED;
                } else {
                    nextStatus = AnalysisStatusEnum.FAILED;
                    failMessage = event.getMessage();
                }


                this.notifyAnalysisStatusChange(job.getAnalysis().getId(), nextStatus, null, failMessage);
            }

        } catch (JobService.NoSuchJobException e) {
            log.error("Job not found", e);
        } catch (JobExecutionException e) {
            log.error("Job cannot be stopped", e);
        }
    }

    public class JobExecutionException extends Exception {
        public JobExecutionException(String message) {
            super(message);
        }

        public JobExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

package it.unimib.disco.bigtwine.services.jobsupervisor.domain;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.enumeration.JobType;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobProcess;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

@Document(collection = "jobs")
@CompoundIndexes({
    @CompoundIndex(def = "{ 'analysis.id': 1 }", name = "analysis_id")
})
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("job_type")
    private JobType jobType;

    @NotNull
    @Field("analysis")
    private AnalysisInfo analysis;

    @Field("reference")
    private String reference;

    @Field("process")
    private JobProcess process;

    @Field("start_date")
    private Instant startDate;

    @Field("end_date")
    private Instant endDate;

    @Field("end_reason")
    private String endReason;

    @Field("last_update_date")
    private Instant lastUpdateDate;

    @Field("last_heartbeat_date")
    private Instant lastHeartbeatDate;

    @Field("running")
    private boolean running;

    @Field("progress")
    private double progress;


    public Job() {
        this.jobType = JobType.DEFAULT;
        this.progress = -1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public AnalysisInfo getAnalysis() {
        return analysis;
    }

    public void setAnalysis(AnalysisInfo analysis) {
        this.analysis = analysis;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public JobProcess getProcess() {
        return process;
    }

    public void setProcess(JobProcess process) {
        this.process = process;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getEndReason() {
        return endReason;
    }

    public void setEndReason(String endReason) {
        this.endReason = endReason;
    }

    public Instant getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Instant lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Instant getLastHeartbeatDate() {
        return lastHeartbeatDate;
    }

    public void setLastHeartbeatDate(Instant lastHeartbeatDate) {
        this.lastHeartbeatDate = lastHeartbeatDate;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "Job{" +
            "id='" + id + '\'' +
            ", jobType=" + jobType +
            ", analysis=" + analysis +
            ", reference='" + reference + '\'' +
            ", process=" + process +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", endReason='" + endReason + '\'' +
            ", lastUpdateDate=" + lastUpdateDate +
            ", lastHeartbeatDate=" + lastHeartbeatDate +
            ", running=" + running +
            ", progress=" + progress +
            '}';
    }
}

package it.unimib.disco.bigtwine.commons.messaging;

import it.unimib.disco.bigtwine.commons.models.JobActionEnum;
import it.unimib.disco.bigtwine.commons.models.JobTypeEnum;

public class JobControlEvent implements Event {
    private String analysisId;
    private String jobId;
    private JobTypeEnum jobType;
    private JobActionEnum action;
    private String reference;

    public JobControlEvent() {
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public JobTypeEnum getJobType() {
        return jobType;
    }

    public void setJobType(JobTypeEnum jobType) {
        this.jobType = jobType;
    }

    public JobActionEnum getAction() {
        return action;
    }

    public void setAction(JobActionEnum action) {
        this.action = action;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}

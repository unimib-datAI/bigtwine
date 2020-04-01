package it.unimib.disco.bigtwine.commons.messaging;

import it.unimib.disco.bigtwine.commons.models.AnalysisStatusEnum;

public class AnalysisStatusChangedEvent implements Event {
    private String analysisId;
    private AnalysisStatusEnum status;
    private String message;
    private Object user;

    public AnalysisStatusChangedEvent() {

    }

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public AnalysisStatusEnum getStatus() {
        return status;
    }

    public void setStatus(AnalysisStatusEnum status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }
}

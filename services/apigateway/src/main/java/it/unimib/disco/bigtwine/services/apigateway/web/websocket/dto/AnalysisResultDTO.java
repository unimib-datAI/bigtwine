package it.unimib.disco.bigtwine.services.apigateway.web.websocket.dto;

import java.time.OffsetDateTime;

public class AnalysisResultDTO {
    private String id;
    private String analysisId;
    private OffsetDateTime processDate;
    private OffsetDateTime saveDate;
    private Object payload;

    public AnalysisResultDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public OffsetDateTime getProcessDate() {
        return processDate;
    }

    public void setProcessDate(OffsetDateTime processDate) {
        this.processDate = processDate;
    }

    public OffsetDateTime getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(OffsetDateTime saveDate) {
        this.saveDate = saveDate;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}

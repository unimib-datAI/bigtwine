package it.unimib.disco.bigtwine.services.apigateway.web.websocket.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class AnalysisDTO {
    private String id;
    private String type = null;
    private Object owner;
    private String status = null;
    private List<Object> statusHistory = null;
    private String visibility = null;
    private Object input = null;
    private double progress;
    private List<Object> exports = null;
    private Map<String, Object> settings = null;
    private OffsetDateTime createDate;
    private OffsetDateTime updateDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getOwner() {
        return owner;
    }

    public void setOwner(Object owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Object> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<Object> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public List<Object> getExports() {
        return exports;
    }

    public void setExports(List<Object> exports) {
        this.exports = exports;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
    }

    public OffsetDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(OffsetDateTime updateDate) {
        this.updateDate = updateDate;
    }
}

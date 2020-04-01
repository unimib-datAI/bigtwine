package it.unimib.disco.bigtwine.services.analysis.domain;

import it.unimib.disco.bigtwine.services.analysis.config.Constants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisVisibility;

/**
 * A Analysis.
 */
@Document(collection = Constants.ANALYSIS_DB_COLLECTION)
public class Analysis implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final AnalysisStatus DEFAULT_STATUS = AnalysisStatus.READY;
    public static final AnalysisVisibility DEFAULT_VISIBILITY = AnalysisVisibility.PUBLIC;

    @Id
    private String id;

    @NotNull
    @Field("type")
    private AnalysisType type;

    @NotNull
    @Field("status")
    private AnalysisStatus status;

    @Field("status_history")
    private List<AnalysisStatusHistory> statusHistory;

    @NotNull
    @Field("visibility")
    private AnalysisVisibility visibility;

    @NotNull
    @Field("owner")
    private User owner;

    @NotNull
    @Field("create_date")
    private Instant createDate;

    @NotNull
    @Field("update_date")
    private Instant updateDate;

    @Field("input")
    private AnalysisInput input;

    @Field("progress")
    private double progress;

    @Field("results_count")
    private long resultsCount;

    @Field("settings")
    private Map<String, Object> settings = null;

    @Field("exports")
    private List<AnalysisExport> exports;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AnalysisType getType() {
        return type;
    }

    public Analysis type(AnalysisType type) {
        this.type = type;
        return this;
    }

    public void setType(AnalysisType type) {
        this.type = type;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public Analysis status(AnalysisStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }

    public List<AnalysisStatusHistory> getStatusHistory() {
        return statusHistory;
    }

    public Analysis statusHistory(List<AnalysisStatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
        return this;
    }

    public void setStatusHistory(List<AnalysisStatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public void addStatusChange(@NotNull AnalysisStatusHistory change) {
        if (this.statusHistory == null) {
            this.statusHistory = new ArrayList<>();
        }

        this.statusHistory.add(change);
    }

    public AnalysisVisibility getVisibility() {
        return visibility;
    }

    public Analysis visibility(AnalysisVisibility visibility) {
        this.visibility = visibility;
        return this;
    }

    public void setVisibility(AnalysisVisibility visibility) {
        this.visibility = visibility;
    }

    public User getOwner() {
        return owner;
    }

    public Analysis owner(User owner) {
        this.owner = owner;
        return this;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public Analysis createDate(Instant createDate) {
        this.createDate = createDate;
        return this;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public Analysis updateDate(Instant updateDate) {
        this.updateDate = updateDate;
        return this;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public AnalysisInput getInput() {
        return input;
    }

    public Analysis input(AnalysisInput input) {
        this.setInput(input);
        return this;
    }

    public void setInput(AnalysisInput input) {
        this.input = input;
    }

    public double getProgress() {
        return progress;
    }

    public Analysis progress(double progress) {
        this.setProgress(progress);
        return this;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public long getResultsCount() {
        return resultsCount;
    }

    public Analysis resultsCount(long resultsCount) {
        this.setResultsCount(resultsCount);
        return this;
    }

    public void setResultsCount(long resultsCount) {
        this.resultsCount = resultsCount;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public Analysis settings(Map<String, Object> settings) {
        this.setSettings(settings);
        return this;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public List<AnalysisExport> getExports() {
        return exports;
    }

    public Analysis exports(List<AnalysisExport> exports) {
        this.setExports(exports);
        return this;
    }

    public void setExports(List<AnalysisExport> exports) {
        this.exports = exports;
    }

    public void addExport(@NotNull AnalysisExport export) {
        if (this.exports == null) {
            this.exports = new ArrayList<>();
        }

        this.exports.add(export);
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Analysis analysis = (Analysis) o;
        if (analysis.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), analysis.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Analysis{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", status='" + getStatus() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", owner='" + getOwner() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", input='" + getInput() + "'" +
            ", progress='" + getProgress() + "'" +
            ", exports='" + getExports() + "'" +
            "}";
    }
}

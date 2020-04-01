package it.unimib.disco.bigtwine.services.analysis.domain;

import it.unimib.disco.bigtwine.services.analysis.config.Constants;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A AnalysisSettingCollection.
 */
@Document(collection = Constants.ANALYSIS_SETTING_COLLECTION_DB_COLLECTION)
@CompoundIndexes({
    @CompoundIndex(def = "{'analysisType': 1, 'analysisInputType': 1}", unique = true, name = "type_input_type_unique")
})
public class AnalysisSettingCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @DBRef
    @Field("settings")
    private Set<AnalysisSetting> settings = new HashSet<>();

    @Field("analysisType")
    private AnalysisType analysisType;

    @Field("analysisInputType")
    private AnalysisInputType analysisInputType;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<AnalysisSetting> getSettings() {
        return settings;
    }

    public AnalysisSettingCollection settings(Set<AnalysisSetting> analysisSettings) {
        this.settings = analysisSettings;
        return this;
    }

    public void setSettings(Set<AnalysisSetting> analysisSettings) {
        this.settings = analysisSettings;
    }

    public AnalysisType getAnalysisType() {
        return analysisType;
    }

    public AnalysisSettingCollection analysisType(AnalysisType analysisType) {
        this.analysisType = analysisType;
        return this;
    }

    public void setAnalysisType(AnalysisType analysisType) {
        this.analysisType = analysisType;
    }

    public AnalysisInputType getAnalysisInputType() {
        return analysisInputType;
    }

    public AnalysisSettingCollection analysisInputType(AnalysisInputType analysisInputType) {
        this.analysisInputType = analysisInputType;
        return this;
    }

    public void setAnalysisInputType(AnalysisInputType analysisInputType) {
        this.analysisInputType = analysisInputType;
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
        AnalysisSettingCollection analysisSettingCollection = (AnalysisSettingCollection) o;
        if (analysisSettingCollection.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), analysisSettingCollection.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AnalysisSettingCollection{" +
            "id='" + id + '\'' +
            ", analysisType=" + analysisType +
            ", analysisInputType=" + analysisInputType +
            '}';
    }
}

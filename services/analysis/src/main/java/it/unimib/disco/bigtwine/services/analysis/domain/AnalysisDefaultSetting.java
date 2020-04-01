package it.unimib.disco.bigtwine.services.analysis.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.unimib.disco.bigtwine.services.analysis.config.Constants;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A AnalysisDefaultSetting.
 */
@Document(collection = Constants.ANALYSIS_DEFAULT_SETTINGS_DB_COLLECTION)
public class AnalysisDefaultSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("default_value")
    private Object defaultValue;

    @Field("user_can_override")
    private Boolean userCanOverride;

    @Field("priority")
    private Integer priority;

    @DBRef
    @Field("setting")
    @JsonIgnoreProperties("")
    private AnalysisSetting setting;

    @Field("analysis_type")
    private AnalysisType analysisType;

    @Field("analysis_input_types")
    private Set<AnalysisInputType> analysisInputTypes = new HashSet<>();

    @Field("user_roles")
    private Set<String> userRoles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public AnalysisDefaultSetting defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean isUserCanOverride() {
        return userCanOverride;
    }

    public AnalysisDefaultSetting userCanOverride(Boolean userCanOverride) {
        this.userCanOverride = userCanOverride;
        return this;
    }

    public void setUserCanOverride(Boolean userCanOverride) {
        this.userCanOverride = userCanOverride;
    }

    public Integer getPriority() {
        return priority;
    }

    public AnalysisDefaultSetting priority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public AnalysisType getAnalysisType() {
        return analysisType;
    }

    public AnalysisDefaultSetting analysisType(AnalysisType analysisType) {
        this.analysisType = analysisType;
        return this;
    }

    public void setAnalysisType(AnalysisType analysisType) {
        this.analysisType = analysisType;
    }

    public AnalysisSetting getSetting() {
        return setting;
    }

    public AnalysisDefaultSetting setting(AnalysisSetting analysisSetting) {
        this.setting = analysisSetting;
        return this;
    }

    public void setSetting(AnalysisSetting analysisSetting) {
        this.setting = analysisSetting;
    }

    public Set<AnalysisInputType> getAnalysisInputTypes() {
        return analysisInputTypes;
    }

    public AnalysisDefaultSetting analysisInputTypes(Set<AnalysisInputType> analysisInputTypes) {
        this.analysisInputTypes = analysisInputTypes;
        return this;
    }

    public AnalysisDefaultSetting addAnalysisInputTypes(AnalysisInputType analysisInputType) {
        this.analysisInputTypes.add(analysisInputType);
        return this;
    }

    public AnalysisDefaultSetting removeAnalysisInputTypes(AnalysisInputType analysisInputType) {
        this.analysisInputTypes.remove(analysisInputType);
        return this;
    }

    public void setAnalysisInputTypes(Set<AnalysisInputType> analysisInputTypes) {
        this.analysisInputTypes = analysisInputTypes;
    }

    public Set<String> getUserRoles() {
        return userRoles;
    }

    public AnalysisDefaultSetting userRoles(Set<String> authorities) {
        this.userRoles = authorities;
        return this;
    }

    public AnalysisDefaultSetting addUserRoles(String authority) {
        this.userRoles.add(authority);
        return this;
    }

    public AnalysisDefaultSetting removeUserRoles(String authority) {
        this.userRoles.remove(authority);
        return this;
    }

    public void setUserRoles(Set<String> authorities) {
        this.userRoles = authorities;
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
        AnalysisDefaultSetting analysisDefaultSetting = (AnalysisDefaultSetting) o;
        if (analysisDefaultSetting.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), analysisDefaultSetting.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AnalysisDefaultSetting{" +
            "id=" + getId() +
            ", defaultValue='" + getDefaultValue() + "'" +
            ", userCanOverride='" + isUserCanOverride() + "'" +
            ", priority=" + getPriority() +
            "}";
    }
}

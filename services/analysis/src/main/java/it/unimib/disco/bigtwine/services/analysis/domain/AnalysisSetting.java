package it.unimib.disco.bigtwine.services.analysis.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import it.unimib.disco.bigtwine.services.analysis.config.Constants;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisSettingType;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisSettingVisibility;

/**
 * A AnalysisSetting.
 */
@Document(collection = Constants.ANALYSIS_SETTINGS_DB_COLLECTION)
public class AnalysisSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    /**
     * Setting name, should be alphanumeric and lowercase
     */
    @NotNull
    @Pattern(regexp = "[a-z0-9-]+")
    @ApiModelProperty(value = "Setting name, should be alphanumeric and lowercase, can contains dashes", required = true)
    @Indexed(unique = true)
    @Field("name")
    private String name;

    @NotNull
    @Field("label")
    private String label;

    @Field("description")
    private String description;

    @NotNull
    @Field("type")
    private AnalysisSettingType type;

    /**
     * Change how the user can interact with a setting
     *
     * GLOBAL: Setting not embedded into analyses and not visible to the user
     * HIDDEN: Setting embedded into analyses but not visible to the user
     * USER_VISIBLE: Setting embedded into analyses and visible to the user
     */
    @NotNull
    @ApiModelProperty(value = "Change how the user can interact with a setting GLOBAL: Setting not embedded into analyses and not visible to the user HIDDEN: Setting embedded into analyses but not visible to the user USER_VISIBLE: Setting embedded into analyses and visible to the user", required = true)
    @Field("visibility")
    private AnalysisSettingVisibility visibility;

    /**
     * Each option pair on separated lines with the following format: <value>:<name>
     */
    @ApiModelProperty(value = "Each option pair on separated lines with the following format: <value>:<name>")
    @Transient
    @JsonSerialize
    @JsonDeserialize
    private String options;

    @Transient
    private boolean _needsRebuildOptions;

    @Field("choices")
    @JsonIgnore
    @AccessType(AccessType.Type.PROPERTY)
    private List<AnalysisSettingChoice> choices = new ArrayList<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public AnalysisSetting name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public AnalysisSetting label(String label) {
        this.label = label;
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public AnalysisSetting description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AnalysisSettingType getType() {
        return type;
    }

    public AnalysisSetting type(AnalysisSettingType type) {
        this.type = type;
        return this;
    }

    public void setType(AnalysisSettingType type) {
        this.type = type;
    }

    public AnalysisSettingVisibility getVisibility() {
        return visibility;
    }

    public AnalysisSetting visibility(AnalysisSettingVisibility visibility) {
        this.visibility = visibility;
        return this;
    }

    public void setVisibility(AnalysisSettingVisibility visibility) {
        this.visibility = visibility;
    }

    public boolean isGlobal() {
        return AnalysisSettingVisibility.GLOBAL.equals(this.visibility);
    }

    public boolean isHidden() {
        return AnalysisSettingVisibility.HIDDEN.equals(this.visibility);
    }

    public boolean isUserVisible() {
        return AnalysisSettingVisibility.USER_VISIBLE.equals(this.visibility);
    }

    public String getOptions() {
        if (this._needsRebuildOptions) {
            this.rebuildOptions();
        }

        return options;
    }

    public AnalysisSetting options(String options) {
        this.setOptions(options);
        return this;
    }

    public void setOptions(String options) {
        this.options = options;
        this.rebuildChoices();
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    public List<AnalysisSettingChoice> getChoices() {
        return this.choices;
    }

    public AnalysisSetting choices(List<AnalysisSettingChoice> choices) {
        this.setChoices(choices);
        return this;
    }

    public void setChoices(List<AnalysisSettingChoice> choices) {
        this.choices = choices;
        this._needsRebuildOptions = true;
    }

    public AnalysisSetting addChoice(AnalysisSettingChoice choice) {
        if (this.choices == null) {
            this.choices = new ArrayList<>();
        }

        this.choices.add(choice);
        this._needsRebuildOptions = true;

        return this;
    }

    private void rebuildChoices() {
        if (StringUtils.isBlank(this.options)) {
            this.choices = Collections.emptyList();
        } else {
            List<AnalysisSettingChoice> choices = new ArrayList<>();
            String[] lines = this.options.split("\\r?\\n");
            ObjectMapper mapper = new ObjectMapper();

            for (String line: lines) {
                String[] parts = line.split(":");

                if (parts.length != 2 || StringUtils.isBlank(parts[0]) || StringUtils.isBlank(parts[1])) {
                    continue;
                }

                String strValue = parts[0].trim();
                Object value;
                try {
                    JsonNode node = mapper.readTree(strValue);
                    if (node.isNumber()) {
                        value = node.numberValue();
                    } else if (node.isBoolean()) {
                        value = node.booleanValue();
                    } else {
                        value = strValue;
                    }
                } catch (Exception e) {
                    value = strValue;
                }

                choices.add(new AnalysisSettingChoice()
                    .value(value)
                    .name(parts[1].trim()));
            }

            this.choices = choices;
        }
    }

    private void rebuildOptions() {
        if (choices != null && choices.size() > 0) {
            StringBuilder optionsBuilder = new StringBuilder();
            for (AnalysisSettingChoice choice : choices) {
                optionsBuilder.append(String.format("%s:%s\n", choice.getValue().toString(), choice.getName()));
            }

            this.options = optionsBuilder.toString();
        } else {
            this.options = "";
        }
        this._needsRebuildOptions = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnalysisSetting analysisSetting = (AnalysisSetting) o;
        if (analysisSetting.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), analysisSetting.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AnalysisSetting{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", label='" + getLabel() + "'" +
            ", description='" + getDescription() + "'" +
            ", type='" + getType() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", options='" + getOptions() + "'" +
            "}";
    }
}

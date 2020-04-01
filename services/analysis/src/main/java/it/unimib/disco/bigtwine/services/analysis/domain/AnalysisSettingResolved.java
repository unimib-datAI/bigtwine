package it.unimib.disco.bigtwine.services.analysis.domain;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisSettingType;

import java.util.List;

public class AnalysisSettingResolved {
    private String name;
    private String label;
    private AnalysisSettingType type;
    private boolean editable;
    private String description;
    private List<AnalysisSettingChoice> choices;
    private Object defaultValue;
    private Object currentValue;
    private boolean isAnalysisTypeRestricted;
    private boolean isInputTypeRestricted;
    private boolean isUserRolesRestricted;

    public AnalysisSettingResolved() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnalysisSettingResolved name(String name) {
        this.setName(name);
        return this;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public AnalysisSettingResolved label(String label) {
        this.setLabel(label);
        return this;
    }

    public AnalysisSettingType getType() {
        return type;
    }

    public void setType(AnalysisSettingType type) {
        this.type = type;
    }

    public AnalysisSettingResolved type(AnalysisSettingType type) {
        this.setType(type);
        return this;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public AnalysisSettingResolved editable(boolean editable) {
        this.setEditable(editable);
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AnalysisSettingResolved description(String description) {
        this.setDescription(description);
        return this;
    }

    public List<AnalysisSettingChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<AnalysisSettingChoice> choices) {
        this.choices = choices;
    }

    public AnalysisSettingResolved choices(List<AnalysisSettingChoice> choices) {
        this.setChoices(choices);
        return this;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public AnalysisSettingResolved defaultValue(Object defaultValue) {
        this.setDefaultValue(defaultValue);
        return this;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }

    public AnalysisSettingResolved currentValue(Object currentValue) {
        this.setCurrentValue(currentValue);
        return this;
    }

    public boolean isAnalysisTypeRestricted() {
        return isAnalysisTypeRestricted;
    }

    public AnalysisSettingResolved isAnalysisTypeRestricted(boolean isAnalysisTypeRestricted) {
        this.setAnalysisTypeRestricted(isAnalysisTypeRestricted);
        return this;
    }

    public void setAnalysisTypeRestricted(boolean isAnalysisTypeRestricted) {
        this.isAnalysisTypeRestricted = isAnalysisTypeRestricted;
    }

    public boolean isInputTypeRestricted() {
        return isInputTypeRestricted;
    }

    public AnalysisSettingResolved isInputTypeRestricted(boolean isInputTypeRestricted) {
        this.setInputTypeRestricted(isInputTypeRestricted);
        return this;
    }

    public void setInputTypeRestricted(boolean inputTypeRestricted) {
        isInputTypeRestricted = inputTypeRestricted;
    }

    public boolean isUserRolesRestricted() {
        return isUserRolesRestricted;
    }

    public AnalysisSettingResolved isUserRolesRestricted(boolean isUserRolesRestricted) {
        this.setUserRolesRestricted(isUserRolesRestricted);
        return this;
    }

    public void setUserRolesRestricted(boolean userRolesRestricted) {
        isUserRolesRestricted = userRolesRestricted;
    }

    @Override
    public String toString() {
        return "AnalysisSettingResolved{" +
            "name='" + name + '\'' +
            ", label=" + label +
            ", type=" + type +
            ", editable=" + editable +
            ", description='" + description + '\'' +
            ", choices=" + choices +
            ", defaultValue=" + defaultValue +
            ", currentValue=" + currentValue +
            ", isAnalysisTypeRestricted=" + isAnalysisTypeRestricted +
            ", isInputTypeRestricted=" + isInputTypeRestricted +
            ", isUserRolesRestricted=" + isUserRolesRestricted +
            '}';
    }
}

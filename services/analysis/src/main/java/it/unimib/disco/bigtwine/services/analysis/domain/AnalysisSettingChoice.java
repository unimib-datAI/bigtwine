package it.unimib.disco.bigtwine.services.analysis.domain;

import javax.validation.constraints.NotNull;

public class AnalysisSettingChoice {
    @NotNull
    private String name;

    @NotNull
    private Object value;

    public AnalysisSettingChoice() {
    }

    public AnalysisSettingChoice(@NotNull String name, @NotNull Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnalysisSettingChoice name(String name) {
        this.setName(name);
        return this;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public AnalysisSettingChoice value(Object value) {
        this.setValue(value);
        return this;
    }

    @Override
    public String toString() {
        return "AnalysisSettingOption{" +
            "name='" + name + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}

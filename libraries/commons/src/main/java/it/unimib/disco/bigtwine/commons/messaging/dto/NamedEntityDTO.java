package it.unimib.disco.bigtwine.commons.messaging.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class NamedEntityDTO implements Serializable {
    private String value;
    private String label;
    private float probability;

    public NamedEntityDTO() {

    }

    public NamedEntityDTO(@NotNull String value, @NotNull String label, float probability) {
        this.value = value;
        this.label = label;
        this.probability = probability;
    }

    public String getValue() {
        return value;
    }

    public void setValue(@NotNull String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(@NotNull String label) {
        this.label = label;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }
}

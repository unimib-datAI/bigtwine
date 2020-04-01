package it.unimib.disco.bigtwine.services.analysis.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;

import java.util.List;
import java.util.Objects;

public class GeoAreaAnalysisInput implements AnalysisInput {
    private String description;
    private List<BoundingBox> boundingBoxes;

    public GeoAreaAnalysisInput() {
    }

    @Override
    @JsonProperty("type")
    public AnalysisInputType getType() {
        return AnalysisInputType.GEO_AREA;
    }

    @Override
    @JsonProperty("isBounded")
    public boolean isBounded() {
        return false;
    }

    public String getDescription() {
        return description;
    }

    public GeoAreaAnalysisInput description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    public GeoAreaAnalysisInput boundingBoxes(List<BoundingBox> boundingBoxes) {
        this.boundingBoxes = boundingBoxes;
        return this;
    }

    public void setBoundingBoxes(List<BoundingBox> boundingBoxes) {
        this.boundingBoxes = boundingBoxes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDescription(), this.getBoundingBoxes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoAreaAnalysisInput input = (GeoAreaAnalysisInput) o;
        if (input.getBoundingBoxes() == null || getBoundingBoxes() == null ||
            input.getDescription() == null || getDescription() == null) {
            return false;
        }
        return Objects.equals(getBoundingBoxes(), input.getBoundingBoxes()) &&
            Objects.equals(getDescription(), input.getDescription());
    }

    @Override
    public String toString() {
        return "GeoAreaAnalysisInput{" +
            "description='" + description + '\'' +
            ", boundingBoxes=" + boundingBoxes +
            '}';
    }
}

package it.unimib.disco.bigtwine.services.analysis.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;

import java.util.Objects;

public class DatasetAnalysisInput implements AnalysisInput {
    private String documentId;
    private String name;
    private long size;

    public DatasetAnalysisInput() {
    }

    @JsonProperty("type")
    @Override
    public AnalysisInputType getType() {
        return AnalysisInputType.DATASET;
    }

    @Override
    @JsonProperty("isBounded")
    public boolean isBounded() {
        return true;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public DatasetAnalysisInput documentId(String documentId) {
        this.setDocumentId(documentId);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DatasetAnalysisInput name(String name) {
        this.setName(name);
        return this;
    }

    public long getSize() {
        return size;
    }

    public DatasetAnalysisInput size(long size) {
        this.setSize(size);
        return this;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getDocumentId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DatasetAnalysisInput input = (DatasetAnalysisInput) o;
        if (input.getDocumentId() == null || getDocumentId() == null) {
            return false;
        }
        return Objects.equals(getDocumentId(), input.getDocumentId());
    }
}

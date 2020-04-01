package it.unimib.disco.bigtwine.services.analysis.domain;

import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

public class AnalysisExport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Field("document_id")
    private String documentId;

    @Field("format")
    private String format;

    @Field("progress")
    private double progress;

    @Field("completed")
    private boolean completed;

    @Field("failed")
    private boolean failed;

    @Field("message")
    private String message;

    public AnalysisExport() {
    }

    public String getDocumentId() {
        return documentId;
    }

    public AnalysisExport documentId(String documentId) {
        this.setDocumentId(documentId);
        return this;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public double getProgress() {
        return progress;
    }

    public String getFormat() {
        return format;
    }

    public AnalysisExport format(String format) {
        this.setFormat(format);
        return this;
    }

    public AnalysisExport setFormat(String format) {
        this.format = format;
        return this;
    }

    public AnalysisExport progress(double progress) {
        this.setProgress(progress);
        return this;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public boolean isCompleted() {
        return completed;
    }

    public AnalysisExport completed(boolean completed) {
        this.setCompleted(completed);
        return this;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isFailed() {
        return failed;
    }

    public AnalysisExport failed(boolean failed) {
        this.setFailed(failed);
        return this;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public String getMessage() {
        return message;
    }

    public AnalysisExport message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AnalysisExport{" +
            "documentId='" + documentId + '\'' +
            ", progress=" + progress +
            ", completed=" + completed +
            ", failed=" + failed +
            ", message='" + message + '\'' +
            '}';
    }
}

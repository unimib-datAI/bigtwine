package it.unimib.disco.bigtwine.services.analysis.domain;

import java.time.Instant;

public class Document {
    private String id;
    private String type;
    private String analysisType;
    private String analysisId;
    private String category;
    private String filename;
    private Long size;
    private String contentType;
    private Instant uploadDate;
    private User user = null;

    public Document() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Document id(String id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Document type(String type) {
        this.type = type;
        return this;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public Document analysisType(String analysisType) {
        this.analysisType = analysisType;
        return this;
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public Document analysisId(String analysisId) {
        this.analysisId = analysisId;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Document category(String category) {
        this.category = category;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Document filename(String filename) {
        this.filename = filename;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Document size(Long size) {
        this.size = size;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Document contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Instant getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Instant uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Document uploadDate(Instant uploadDate) {
        this.uploadDate = uploadDate;
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Document user(User user) {
        this.user = user;
        return this;
    }

    @Override
    public String toString() {
        return "Document{" +
            "id='" + id + '\'' +
            ", type='" + type + '\'' +
            ", category='" + category + '\'' +
            ", filename='" + filename + '\'' +
            ", size=" + size +
            ", contentType='" + contentType + '\'' +
            ", uploadDate=" + uploadDate +
            '}';
    }
}

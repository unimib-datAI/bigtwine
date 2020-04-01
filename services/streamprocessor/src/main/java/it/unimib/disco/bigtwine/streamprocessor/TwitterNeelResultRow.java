package it.unimib.disco.bigtwine.streamprocessor;

import java.io.Serializable;

class TwitterNeelResultRow implements Serializable {
    private String tweetId;
    private Integer positionStart;
    private Integer positionEnd;
    private String resourceUri;
    private Double confidence;
    private String category;

    public TwitterNeelResultRow() {

    }

    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public Integer getPositionStart() {
        return positionStart;
    }

    public void setPositionStart(Integer pos) {
        this.positionStart = pos;
    }

    public Integer getPositionEnd() {
        return positionEnd;
    }

    public void setPositionEnd(Integer pos) {
        this.positionEnd = pos;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "TwitterNeelResultRow{" +
                "tweetId='" + tweetId + '\'' +
                ", positionStart=" + positionStart +
                ", positionEnd=" + positionEnd +
                ", resourceUri='" + resourceUri + '\'' +
                ", confidence=" + confidence +
                ", category='" + category + '\'' +
                '}';
    }
}

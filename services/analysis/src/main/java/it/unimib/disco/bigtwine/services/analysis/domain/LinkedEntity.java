package it.unimib.disco.bigtwine.services.analysis.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

public class LinkedEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Field("position")
    private TextRange position;

    @Indexed
    @Field("value")
    private String value;

    @Indexed
    @Field("link")
    private String link;

    @Field("confidence")
    private float confidence;

    @Field("category")
    private String category;

    @Indexed
    @Field("isNil")
    private boolean isNil;

    @Field("nilCluster")
    private String nilCluster;

    @Field("resource")
    private Resource resource;

    public LinkedEntity() {
    }

    public TextRange getPosition() {
        return position;
    }

    public void setPosition(TextRange position) {
        this.position = position;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isNil() {
        return isNil;
    }

    public void setNil(boolean nil) {
        isNil = nil;
    }

    public boolean getIsNil() {
        return isNil;
    }

    public void setIsNil(boolean nil) {
        isNil = nil;
    }

    public String getNilCluster() {
        return nilCluster;
    }

    public void setNilCluster(String nilCluster) {
        this.nilCluster = nilCluster;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}

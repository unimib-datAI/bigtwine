package it.unimib.disco.bigtwine.services.analysis.domain;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class TwitterStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Indexed
    @NotNull
    @Field("id")
    private String id;

    @TextIndexed
    @NotNull
    @Field("text")
    private String text;

    @Field("user")
    private TwitterUser user;

    @GeoSpatialIndexed
    @Field("coordinates")
    private GeoJsonPoint coordinates;

    public TwitterStatus() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TwitterUser getUser() {
        return user;
    }

    public void setUser(TwitterUser user) {
        this.user = user;
    }

    public GeoJsonPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoJsonPoint coordinates) {
        this.coordinates = coordinates;
    }
}

package it.unimib.disco.bigtwine.services.analysis.domain;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class TwitterUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Indexed
    @NotNull
    @Field("id")
    private String id;

    @NotNull
    @Field("screenName")
    private String screenName;

    @Field("name")
    private String name;

    @Field("location")
    private String location;

    @Field("profileImageUrl")
    private String profileImageUrl;

    @GeoSpatialIndexed
    @Field("coordinates")
    private GeoJsonPoint coordinates;

    public TwitterUser() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public GeoJsonPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoJsonPoint coordinates) {
        this.coordinates = coordinates;
    }
}

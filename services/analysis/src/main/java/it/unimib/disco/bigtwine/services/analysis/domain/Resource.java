package it.unimib.disco.bigtwine.services.analysis.domain;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Map;

public class Resource implements Serializable {
    private static final long serialVersionUID = 1L;


    @Field("name")
    private String name;

    @Field("shortDesc")
    private String shortDesc;

    @Field("thumb")
    private String thumb;

    @Field("thumbLarge")
    private String thumbLarge;

    @Indexed
    @Field("url")
    private String url;

    @GeoSpatialIndexed
    @Field("coordinates")
    private GeoJsonPoint coordinates;

    @Field("extra")
    private Map<String, Object> extra;

    public Resource() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getThumbLarge() {
        return thumbLarge;
    }

    public void setThumbLarge(String thumbLarge) {
        this.thumbLarge = thumbLarge;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GeoJsonPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoJsonPoint coordinates) {
        this.coordinates = coordinates;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}

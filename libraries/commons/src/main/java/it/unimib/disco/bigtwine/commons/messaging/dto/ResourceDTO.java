package it.unimib.disco.bigtwine.commons.messaging.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

public class ResourceDTO implements Serializable {
    private String tag;
    private String name;
    private String shortDesc;
    private String thumb;
    private String thumbLarge;
    private String url;
    private CoordinatesDTO coordinates;
    private Map<String, Object> extra;

    public ResourceDTO() {
    }

    public ResourceDTO(String name, String shortDesc, String thumb, String thumbLarge, String url, CoordinatesDTO coordinates) {
        this.name = name;
        this.shortDesc = shortDesc;
        this.thumb = thumb;
        this.thumbLarge = thumbLarge;
        this.url = url;
        this.coordinates = coordinates;
    }

    public ResourceDTO(String name, String shortDesc, String thumb, String thumbLarge, String url, CoordinatesDTO coordinates, @NotNull String tag) {
        this(name, shortDesc, thumb, thumbLarge, url, coordinates);
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(@NotNull String tag) {
        this.tag = tag;
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

    public CoordinatesDTO getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesDTO coordinates) {
        this.coordinates = coordinates;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}

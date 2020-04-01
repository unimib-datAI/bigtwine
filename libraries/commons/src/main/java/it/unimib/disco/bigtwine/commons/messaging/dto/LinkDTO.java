package it.unimib.disco.bigtwine.commons.messaging.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class LinkDTO implements Serializable {
    private String tag;
    private String url;

    public LinkDTO() {
    }

    public LinkDTO(String url) {
        this.url = url;
    }

    public LinkDTO(String url, @NotNull String tag) {
        this.tag = tag;
        this.url = url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(@NotNull String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

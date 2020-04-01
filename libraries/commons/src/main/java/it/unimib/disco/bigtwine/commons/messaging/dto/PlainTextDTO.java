package it.unimib.disco.bigtwine.commons.messaging.dto;

import javax.validation.constraints.NotNull;

import java.io.Serializable;

public class PlainTextDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tag;
    private String text;

    public PlainTextDTO() {
    }

    public PlainTextDTO(@NotNull String tag, String text) {
        this.tag = tag;
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(@NotNull String tag) {
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

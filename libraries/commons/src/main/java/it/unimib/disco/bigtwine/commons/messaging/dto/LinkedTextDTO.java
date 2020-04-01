package it.unimib.disco.bigtwine.commons.messaging.dto;

import javax.validation.constraints.NotNull;

import java.io.Serializable;

public class LinkedTextDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tag;
    private String text;
    private LinkedEntityDTO[] entities;

    public LinkedTextDTO() {
    }

    public LinkedTextDTO(@NotNull String tag, String text, LinkedEntityDTO[] entities) {
        this.tag = tag;
        this.text = text;
        this.entities = entities;
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

    public LinkedEntityDTO[] getEntities() {
        return entities;
    }

    public void setEntities(LinkedEntityDTO[] entities) {
        this.entities = entities;
    }
}

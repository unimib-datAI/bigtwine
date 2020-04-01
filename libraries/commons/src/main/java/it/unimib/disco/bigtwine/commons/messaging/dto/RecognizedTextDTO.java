package it.unimib.disco.bigtwine.commons.messaging.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class RecognizedTextDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tag;
    private String text;
    private NamedEntityDTO[] entities;

    public RecognizedTextDTO() {
    }

    public RecognizedTextDTO(@NotNull String tag, String text, NamedEntityDTO[] entities) {
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

    public NamedEntityDTO[] getEntities() {
        return entities;
    }

    public void setEntities(NamedEntityDTO[] entities) {
        this.entities = entities;
    }

    public NamedEntityDTO getEntity(int index) {
        return entities[index];
    }
}

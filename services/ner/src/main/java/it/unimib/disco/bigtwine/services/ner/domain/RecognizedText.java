package it.unimib.disco.bigtwine.services.ner.domain;


import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class RecognizedText implements Serializable {
    private String tag;
    private String text;
    private NamedEntity[] entities;

    public RecognizedText() {
    }

    public RecognizedText(@NotNull String tag, String text, NamedEntity[] entities) {
        this.tag = tag;
        this.text = text;
        this.entities = entities;
    }

    public RecognizedText(@NotNull String tag, String text) {
        this(tag, text, new NamedEntity[0]);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NamedEntity[] getEntities() {
        return entities;
    }

    public void setEntities(NamedEntity[] entities) {
        this.entities = entities;
    }

    public NamedEntity getEntity(int index) {
        return entities[index];
    }
}

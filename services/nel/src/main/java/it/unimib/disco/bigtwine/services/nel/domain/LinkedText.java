package it.unimib.disco.bigtwine.services.nel.domain;


import java.io.Serializable;

public class LinkedText implements Serializable {
    private String tag;
    private String text;
    private LinkedEntity[] entities;

    public LinkedText() {

    }

    public LinkedText(String tag, String text, LinkedEntity[] entities) {
        this.tag = tag;
        this.text = text;
        this.entities = entities;
    }

    public LinkedText(String tag, String text) {
        this(tag, text, new LinkedEntity[0]);
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

    public LinkedEntity[] getEntities() {
        return entities;
    }

    public void setEntities(LinkedEntity[] entities) {
        this.entities = entities;
    }

    public LinkedEntity getEntity(int index) {
        return entities[index];
    }
}

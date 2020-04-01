package it.unimib.disco.bigtwine.services.ner.domain;

import java.io.Serializable;

public class PlainText implements Serializable {
    private String tag;
    private String text;

    public PlainText() {
    }

    public PlainText(String tag, String text) {
        this.tag = tag;
        this.text = text;
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

}

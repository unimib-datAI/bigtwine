package it.unimib.disco.bigtwine.services.analysis.domain;

import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

public class TextRange implements Serializable {
    private static final long serialVersionUID = 1L;

    @Field("start")
    private int start;

    @Field("end")
    private int end;

    public TextRange() {
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}

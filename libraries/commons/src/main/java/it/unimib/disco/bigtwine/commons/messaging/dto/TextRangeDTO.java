package it.unimib.disco.bigtwine.commons.messaging.dto;

import java.io.Serializable;

public class TextRangeDTO implements Serializable {
    private int start;
    private int end;

    public TextRangeDTO() {
    }

    public TextRangeDTO(int start, int end) {
        if (start < 0 || end < 0 || end <= start) {
            throw new IllegalArgumentException("both `end` and `start must be >= 0 and `end` > `start`");
        }
        this.start = start;
        this.end = end;
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

package it.unimib.disco.bigtwine.commons.messaging;

import it.unimib.disco.bigtwine.commons.messaging.dto.LinkedTextDTO;

public class NelResponseMessage implements ResponseMessage {
    private String requestId;
    private String linker = "default";
    private LinkedTextDTO[] texts;
    private Status status;

    public NelResponseMessage() {
    }

    public NelResponseMessage(String requestId, String linker, LinkedTextDTO[] texts) {
        this.requestId = requestId;
        this.linker = linker;
        this.texts = texts;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getLinker() {
        return linker;
    }

    public void setLinker(String linker) {
        this.linker = linker;
    }

    public LinkedTextDTO[] getTexts() {
        return texts;
    }

    public void setTexts(LinkedTextDTO[] texts) {
        this.texts = texts;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }
}

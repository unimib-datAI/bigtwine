package it.unimib.disco.bigtwine.commons.messaging;

import it.unimib.disco.bigtwine.commons.messaging.dto.RecognizedTextDTO;

public class NerResponseMessage implements ResponseMessage {
    private String requestId;
    private String recognizer = "default";
    private RecognizedTextDTO[] texts;
    private Status status;

    public NerResponseMessage() {
    }

    public NerResponseMessage(String requestId, String recognizer, RecognizedTextDTO[] texts) {
        this.requestId = requestId;
        this.recognizer = recognizer;
        this.texts = texts;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRecognizer() {
        return recognizer;
    }

    public void setRecognizer(String recognizer) {
        this.recognizer = recognizer;
    }

    public RecognizedTextDTO[] getTexts() {
        return texts;
    }

    public void setTexts(RecognizedTextDTO[] texts) {
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

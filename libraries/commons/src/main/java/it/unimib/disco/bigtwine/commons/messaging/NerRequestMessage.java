package it.unimib.disco.bigtwine.commons.messaging;

import it.unimib.disco.bigtwine.commons.messaging.dto.PlainTextDTO;

public class NerRequestMessage implements RequestMessage {
    private String requestId;
    private String recognizer = "default";
    private PlainTextDTO[] texts;
    private String outputTopic;
    private long timestamp;
    private long expiration;

    public NerRequestMessage() {
        this.timestamp = System.currentTimeMillis();
        this.expiration = -1;
    }

    public NerRequestMessage(String requestId, String recognizer, PlainTextDTO[] texts) {
        this();
        this.requestId = requestId;
        this.recognizer = recognizer;
        this.texts = texts;
    }

    public NerRequestMessage(String requestId, String recognizer, PlainTextDTO[] texts, long expiration) {
        this(requestId, recognizer, texts);
        this.expiration = expiration;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String getOutputTopic() {
        return outputTopic;
    }

    @Override
    public void setOutputTopic(String outputTopic) {
        this.outputTopic = outputTopic;
    }

    public String getRecognizer() {
        return recognizer;
    }

    public void setRecognizer(String recognizer) {
        this.recognizer = recognizer;
    }

    public PlainTextDTO[] getTexts() {
        return texts;
    }

    public void setTexts(PlainTextDTO[] texts) {
        this.texts = texts;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}

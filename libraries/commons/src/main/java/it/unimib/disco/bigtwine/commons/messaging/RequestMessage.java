package it.unimib.disco.bigtwine.commons.messaging;

import java.io.Serializable;

public interface RequestMessage extends Serializable {
    String getRequestId();
    void setRequestId(String requestId);
    String getOutputTopic();
    void setOutputTopic(String topicId);
    long getTimestamp();
    void setTimestamp(long timestamp);
    long getExpiration();
    void setExpiration(long expiration);
}

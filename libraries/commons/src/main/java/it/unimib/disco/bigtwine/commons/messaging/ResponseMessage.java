package it.unimib.disco.bigtwine.commons.messaging;

import java.io.Serializable;

public interface ResponseMessage extends Serializable {
    String getRequestId();
    void setRequestId(String requestId);
    Status getStatus();
    void setStatus(Status status);

    enum Status {
        PROCESSED, PARTIAL, REJECTED, EXPIRED
    }
}

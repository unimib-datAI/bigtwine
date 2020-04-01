package it.unimib.disco.bigtwine.commons.messaging.dto;

import java.io.Serializable;

public class NeelProcessedTweetDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private TwitterStatusDTO status;
    private LinkedEntityDTO[] entities;

    public TwitterStatusDTO getStatus() {
        return status;
    }

    public void setStatus(TwitterStatusDTO status) {
        this.status = status;
    }

    public LinkedEntityDTO[] getEntities() {
        return entities;
    }

    public void setEntities(LinkedEntityDTO[] entities) {
        this.entities = entities;
    }
}

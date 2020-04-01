package it.unimib.disco.bigtwine.services.analysis.domain;

import org.springframework.data.mongodb.core.mapping.Field;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.List;

/**
 * A NeelProcessedTweet.
 */
public class NeelProcessedTweet implements Serializable, AnalysisResultPayload {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Field("status")
    private TwitterStatus status;

    @NotNull
    @Field("entities")
    private List<LinkedEntity> entities;

    public TwitterStatus getStatus() {
        return status;
    }

    public NeelProcessedTweet status(TwitterStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(TwitterStatus status) {
        this.status = status;
    }

    public List<LinkedEntity> getEntities() {
        return entities;
    }

    public NeelProcessedTweet entities(List<LinkedEntity> entities) {
        this.entities = entities;
        return this;
    }

    public void setEntities(List<LinkedEntity> entities) {
        this.entities = entities;
    }


    @Override
    public String toString() {
        return "NeelProcessedTweet{" +
            "tweet_id=" + this.status.getId() +
            "}";
    }
}

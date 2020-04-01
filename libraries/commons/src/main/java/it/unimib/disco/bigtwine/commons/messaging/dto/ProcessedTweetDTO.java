package it.unimib.disco.bigtwine.commons.messaging.dto;

import java.io.Serializable;

public class ProcessedTweetDTO implements Serializable {
    private String id;
    private String text;
    private TwitterUserDTO user;
    private CoordinatesDTO coordinates;
    private LinkedEntityDTO[] entities;

    public ProcessedTweetDTO() {
    }

    public ProcessedTweetDTO(String id) {
        this.id = id;
    }

    public ProcessedTweetDTO(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TwitterUserDTO getUser() {
        return user;
    }

    public void setUser(TwitterUserDTO user) {
        this.user = user;
    }

    public CoordinatesDTO getCoordinates() {
        return coordinates;
    }

    public void setCoordinate(CoordinatesDTO coordinates) {
        this.coordinates = coordinates;
    }

    public LinkedEntityDTO[] getEntities() {
        return entities;
    }

    public void setEntities(LinkedEntityDTO[] entities) {
        this.entities = entities;
    }
}

package it.unimib.disco.bigtwine.commons.messaging.dto;

import java.io.Serializable;

public class TwitterStatusDTO implements Serializable {
    private String id;
    private String text;
    private TwitterUserDTO user;
    private CoordinatesDTO coordinates;

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

    public void setCoordinates(CoordinatesDTO coordinates) {
        this.coordinates = coordinates;
    }
}

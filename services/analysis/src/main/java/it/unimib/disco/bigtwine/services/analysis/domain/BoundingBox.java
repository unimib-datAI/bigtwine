package it.unimib.disco.bigtwine.services.analysis.domain;

import java.io.Serializable;

public class BoundingBox implements Serializable {
    private String name;
    private Coordinates northEastCoords;
    private Coordinates southWestCoords;

    public BoundingBox() {
    }

    public String getName() {
        return name;
    }

    public BoundingBox name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getNorthEastCoords() {
        return northEastCoords;
    }

    public BoundingBox northEastCoords(Coordinates northEastCoords) {
        this.northEastCoords = northEastCoords;
        return this;
    }

    public void setNorthEastCoords(Coordinates northEastCoords) {
        this.northEastCoords = northEastCoords;
    }

    public Coordinates getSouthWestCoords() {
        return southWestCoords;
    }

    public BoundingBox southWestCoords(Coordinates southWestCoords) {
        this.southWestCoords = southWestCoords;
        return this;
    }

    public void setSouthWestCoords(Coordinates southWestCoords) {
        this.southWestCoords = southWestCoords;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
            "name='" + name + '\'' +
            ", northEastCoords=" + northEastCoords +
            ", southWestCoords=" + southWestCoords +
            '}';
    }
}

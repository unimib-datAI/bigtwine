package it.unimib.disco.bigtwine.services.geo.domain;

import java.io.Serializable;

public class DecodedLocation implements Serializable {
    private String tag;
    private String address;
    private Coordinates coordinates;

    public DecodedLocation() {}

    public DecodedLocation(String address, Coordinates coordinates) {
        this.address = address;
        this.coordinates = coordinates;
    }

    public DecodedLocation(String address, Coordinates coordinates, String tag) {
        this(address, coordinates);
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCoordinates(double lat, double lon) {
        this.coordinates = new Coordinates(lat, lon);
    }
}

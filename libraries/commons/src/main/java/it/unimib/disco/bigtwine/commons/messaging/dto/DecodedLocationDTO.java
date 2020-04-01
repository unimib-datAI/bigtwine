package it.unimib.disco.bigtwine.commons.messaging.dto;

import java.io.Serializable;

public class DecodedLocationDTO implements Serializable {
    private String tag;
    private String address;
    private CoordinatesDTO coordinates;

    public DecodedLocationDTO() {}

    public DecodedLocationDTO(String address, CoordinatesDTO coordinates) {
        this.address = address;
        this.coordinates = coordinates;
    }

    public DecodedLocationDTO(String address, CoordinatesDTO coordinates, String tag) {
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

    public CoordinatesDTO getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesDTO coordinates) {
        this.coordinates = coordinates;
    }

    public void setCoordinates(double lat, double lon) {
        this.coordinates = new CoordinatesDTO(lat, lon);
    }
}

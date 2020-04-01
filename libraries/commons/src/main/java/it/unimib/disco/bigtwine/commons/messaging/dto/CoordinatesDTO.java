package it.unimib.disco.bigtwine.commons.messaging.dto;

import java.io.Serializable;

public class CoordinatesDTO implements Serializable {
    private double latitude;
    private double longitude;

    public CoordinatesDTO() {
    }

    public CoordinatesDTO(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "" + this.getLatitude() + "," + this.getLongitude() + "";
    }
}

package it.unimib.disco.bigtwine.services.analysis.domain;

public class Coordinates {
    private double latitude;
    private double longitude;

    public Coordinates() {
    }

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public Coordinates latitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Coordinates longitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

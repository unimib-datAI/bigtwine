package it.unimib.disco.bigtwine.services.geo.domain;

import java.io.Serializable;

public class Location implements Serializable {
    private String tag;
    private String address;

    public Location() {}

    public Location(String address) {
        this.address = address;
    }

    public Location(String address, String tag) {
        this(address);
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
}

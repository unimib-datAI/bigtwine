package it.unimib.disco.bigtwine.commons.messaging.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class LocationDTO implements Serializable {
    private String tag;
    private String address;

    public LocationDTO() {}

    public LocationDTO(String address) {
        this.address = address;
    }

    public LocationDTO(String address, @NotNull String tag) {
        this(address);
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(@NotNull String tag) {
        this.tag = tag;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

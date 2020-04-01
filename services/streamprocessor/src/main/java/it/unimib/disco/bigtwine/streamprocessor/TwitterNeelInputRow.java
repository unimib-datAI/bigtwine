package it.unimib.disco.bigtwine.streamprocessor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id",
        "text",
        // "coordinates",
        "user__id",
        "user__name",
        "user__location",
})
public class TwitterNeelInputRow {
    @JsonProperty("id")
    private String statusId;

    @JsonProperty("text")
    private String statusText;

    // @JsonProperty("coordinates")
    // private String statusCoordinates;

    @JsonProperty("user__id")
    private String statusUserId;

    @JsonProperty("user__name")
    private String statusUserName;

    @JsonProperty("user__location")
    private String statusUserLocation;

    public TwitterNeelInputRow() {
    }

    public String getStatusId() {
        return statusId;
    }

    public TwitterNeelInputRow setStatusId(String statusId) {
        this.statusId = statusId;
        return this;
    }

    public String getStatusText() {
        return statusText;
    }

    public TwitterNeelInputRow setStatusText(String statusText) {
        this.statusText = statusText;
        return this;
    }

    // public String getStatusCoordinates() {
    //     return statusCoordinates;
    // }

    // public TwitterNeelInputRow setStatusCoordinates(String statusCoordinates) {
    //     this.statusCoordinates = statusCoordinates;
    //     return this;
    // }

    public String getStatusUserId() {
        return statusUserId;
    }

    public TwitterNeelInputRow setStatusUserId(String statusUserId) {
        this.statusUserId = statusUserId;
        return this;
    }

    public String getStatusUserName() {
        return statusUserName;
    }

    public TwitterNeelInputRow setStatusUserName(String statusUserName) {
        this.statusUserName = statusUserName;
        return this;
    }

    public String getStatusUserLocation() {
        return statusUserLocation;
    }

    public TwitterNeelInputRow setStatusUserLocation(String statusUserLocation) {
        this.statusUserLocation = statusUserLocation;
        return this;
    }
}

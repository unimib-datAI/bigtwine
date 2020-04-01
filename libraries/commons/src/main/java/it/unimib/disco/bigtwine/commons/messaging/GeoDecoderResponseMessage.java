package it.unimib.disco.bigtwine.commons.messaging;

import it.unimib.disco.bigtwine.commons.messaging.dto.DecodedLocationDTO;

public class GeoDecoderResponseMessage implements ResponseMessage {
    private String requestId;
    private String decoder = "default";
    private DecodedLocationDTO[] locations;
    private Status status;

    public GeoDecoderResponseMessage() {
    }

    public GeoDecoderResponseMessage(String requestId, String decoder, DecodedLocationDTO[] locations) {
        this.requestId = requestId;
        this.decoder = decoder;
        this.locations = locations;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDecoder() {
        return decoder;
    }

    public void setDecoder(String decoder) {
        this.decoder = decoder;
    }

    public DecodedLocationDTO[] getLocations() {
        return locations;
    }

    public void setLocations(DecodedLocationDTO[] locations) {
        this.locations = locations;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }
}

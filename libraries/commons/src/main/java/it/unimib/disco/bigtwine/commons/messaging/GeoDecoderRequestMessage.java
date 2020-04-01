package it.unimib.disco.bigtwine.commons.messaging;

import it.unimib.disco.bigtwine.commons.messaging.dto.LocationDTO;

public class GeoDecoderRequestMessage implements RequestMessage {
    private String requestId;
    private String outputTopic;
    private long timestamp;
    private long expiration;
    private String decoder = "default";
    private LocationDTO[] locations;

    public GeoDecoderRequestMessage() {
        this.timestamp = System.currentTimeMillis();
        this.expiration = -1;
    }

    public GeoDecoderRequestMessage(String requestId, String decoder, LocationDTO[] locations) {
        this();
        this.requestId = requestId;
        this.decoder = decoder;
        this.locations = locations;
    }

    public GeoDecoderRequestMessage(String requestId, String decoder, LocationDTO[] locations, String outputTopicId) {
        this(requestId, decoder, locations);
        this.outputTopic = outputTopicId;
    }

    public GeoDecoderRequestMessage(String requestId, String decoder, LocationDTO[] locations, String outputTopicId, long expiration) {
        this(requestId, decoder, locations, outputTopicId);
        this.expiration = expiration;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String getOutputTopic() {
        return outputTopic;
    }

    @Override
    public void setOutputTopic(String outputTopic) {
        this.outputTopic = outputTopic;
    }

    public String getDecoder() {
        return decoder;
    }

    public void setDecoder(String decoder) {
        this.decoder = decoder;
    }

    public LocationDTO[] getLocations() {
        return locations;
    }

    public void setLocations(LocationDTO[] locations) {
        this.locations = locations;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}

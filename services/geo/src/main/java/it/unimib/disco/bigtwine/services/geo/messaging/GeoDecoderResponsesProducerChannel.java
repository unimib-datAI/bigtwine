package it.unimib.disco.bigtwine.services.geo.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface GeoDecoderResponsesProducerChannel {
    String CHANNEL = "geoDecoderResponsesChannel";

    @Output
    MessageChannel geoDecoderResponsesChannel();
}

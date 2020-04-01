package it.unimib.disco.bigtwine.services.geo.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface GeoDecoderRequestsConsumerChannel {
    String CHANNEL = "geoDecoderRequestsChannel";

    @Input
    SubscribableChannel geoDecoderRequestsChannel();
}

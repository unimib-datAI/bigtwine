package it.unimib.disco.bigtwine.streamprocessor.response;

import it.unimib.disco.bigtwine.commons.messaging.GeoDecoderResponseMessage;
import it.unimib.disco.bigtwine.commons.messaging.dto.DecodedLocationDTO;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class GeoDecoderResponseMessageParser extends AbstractResponseMessageParser<GeoDecoderResponseMessage, DecodedLocationDTO>
    implements FlatMapFunction<String, DecodedLocationDTO> {
    private static final Logger LOG = LoggerFactory.getLogger(GeoDecoderResponseMessageParser.class);
    private transient JsonDeserializer<GeoDecoderResponseMessage> deserializer;

    public GeoDecoderResponseMessageParser() {
    }

    public GeoDecoderResponseMessageParser(String outputTopic) {
        super(outputTopic);
    }

    @Override
    public JsonDeserializer<GeoDecoderResponseMessage> getDeserializer() {
        if (deserializer == null) {
            deserializer = new JsonDeserializer<>(GeoDecoderResponseMessage.class);
        }
        return deserializer;
    }

    @Override
    public void flatMap(String value, Collector<DecodedLocationDTO> out) throws Exception {
        GeoDecoderResponseMessage res = this.parse(value);

        for (DecodedLocationDTO tweet : res.getLocations())  {
            out.collect(tweet);
        }

        LOG.debug("Finished geo decoding, {} locations collected", res.getLocations().length);
    }
}

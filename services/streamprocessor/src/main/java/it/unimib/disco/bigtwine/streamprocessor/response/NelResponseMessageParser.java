package it.unimib.disco.bigtwine.streamprocessor.response;

import it.unimib.disco.bigtwine.commons.messaging.NelResponseMessage;
import it.unimib.disco.bigtwine.commons.messaging.dto.LinkedTextDTO;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class NelResponseMessageParser extends AbstractResponseMessageParser<NelResponseMessage, LinkedTextDTO>
    implements FlatMapFunction<String, LinkedTextDTO> {
    private static final Logger LOG = LoggerFactory.getLogger(NelResponseMessageParser.class);
    private transient JsonDeserializer<NelResponseMessage> deserializer;

    public NelResponseMessageParser() {
    }

    public NelResponseMessageParser(String outputTopic) {
        super(outputTopic);
    }

    @Override
    public JsonDeserializer<NelResponseMessage> getDeserializer() {
        if (deserializer == null) {
            deserializer = new JsonDeserializer<>(NelResponseMessage.class);
        }
        return deserializer;
    }

    @Override
    public void flatMap(String value, Collector<LinkedTextDTO> out) throws Exception {
        NelResponseMessage res = this.parse(value);

        for (LinkedTextDTO tweet : res.getTexts())  {
            out.collect(tweet);
        }

        LOG.debug("Finished nel processing, {} tweets collected", res.getTexts().length);
    }
}

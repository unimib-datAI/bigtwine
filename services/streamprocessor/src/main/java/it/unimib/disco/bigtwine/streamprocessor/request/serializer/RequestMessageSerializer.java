package it.unimib.disco.bigtwine.streamprocessor.request.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unimib.disco.bigtwine.commons.messaging.RequestMessage;
import org.apache.flink.api.common.functions.MapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class RequestMessageSerializer<M extends RequestMessage> implements MapFunction<M, String> {
    private static final Logger LOG = LoggerFactory.getLogger(RequestMessageSerializer.class);

    private String inputTopic;
    private transient JsonSerializer<M> serializer;

    public RequestMessageSerializer() {
    }

    public RequestMessageSerializer(String inputTopic) {
        this.inputTopic = inputTopic;
    }

    private JsonSerializer<M> getSerializer() {
        if (this.serializer == null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            this.serializer = new JsonSerializer<>(mapper);
        }
        return this.serializer;
    }

    private String serialize(M message) {
        return new String(this.getSerializer().serialize(this.inputTopic, message));
    }

    @Override
    public String map(M value) throws Exception {
        LOG.debug("Starting ner serialization: {}, {}",  this.getSerializer() != null, this.inputTopic);
        String s = this.serialize(value);
        LOG.debug("Finished ner serialization {}", s);
        return s;
    }

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
    }
}

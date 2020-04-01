package it.unimib.disco.bigtwine.streamprocessor.response;

import it.unimib.disco.bigtwine.commons.messaging.ResponseMessage;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public abstract class AbstractResponseMessageParser<M extends ResponseMessage,OUT> {
    protected String outputTopic;


    public AbstractResponseMessageParser() {
    }

    public AbstractResponseMessageParser(String outputTopic) {
        this.outputTopic = outputTopic;
    }

    public String getOutputTopic() {
        return outputTopic;
    }

    public void setOutputTopic(String outputTopic) {
        this.outputTopic = outputTopic;
    }

    protected abstract JsonDeserializer<M> getDeserializer();

    protected M parse(String json) {
        return this.getDeserializer().deserialize(this.outputTopic, json.getBytes());
    }
}

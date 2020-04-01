package it.unimib.disco.bigtwine.streamprocessor.response;

import it.unimib.disco.bigtwine.commons.messaging.LinkResolverResponseMessage;
import it.unimib.disco.bigtwine.commons.messaging.dto.ResourceDTO;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkResolverResponseMessageParser extends AbstractResponseMessageParser<LinkResolverResponseMessage, List<ResourceDTO>>
    implements FlatMapFunction<String, List<ResourceDTO>> {
    private static final Logger LOG = LoggerFactory.getLogger(LinkResolverResponseMessageParser.class);
    private transient JsonDeserializer<LinkResolverResponseMessage> deserializer;

    public LinkResolverResponseMessageParser() {
    }

    public LinkResolverResponseMessageParser(String outputTopic) {
        super(outputTopic);
    }

    @Override
    public JsonDeserializer<LinkResolverResponseMessage> getDeserializer() {
        if (deserializer == null) {
            deserializer = new JsonDeserializer<>(LinkResolverResponseMessage.class);
        }
        return deserializer;
    }

    @Override
    public void flatMap(String value, Collector<List<ResourceDTO>> out) throws Exception {
        LinkResolverResponseMessage res = this.parse(value);
        Map<String, List<ResourceDTO>> resourcesByTag = new HashMap<>();

        for (ResourceDTO resource : res.getResources())  {
            String tag = resource.getTag();

            if (!resourcesByTag.containsKey(tag)) {
                resourcesByTag.put(tag, new ArrayList<>());
            }

            resourcesByTag.get(tag).add(resource);
        }

        for (Map.Entry<String, List<ResourceDTO>> entry : resourcesByTag.entrySet()) {
            out.collect(entry.getValue());
        }

        LOG.debug("Finished link resolving, {} resources collected", res.getResources().length);
    }
}

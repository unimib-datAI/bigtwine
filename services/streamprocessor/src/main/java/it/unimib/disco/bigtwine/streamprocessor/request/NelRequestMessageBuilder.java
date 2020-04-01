package it.unimib.disco.bigtwine.streamprocessor.request;

import com.google.common.collect.Iterables;
import it.unimib.disco.bigtwine.commons.messaging.NelRequestMessage;
import it.unimib.disco.bigtwine.commons.messaging.dto.RecognizedTextDTO;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NelRequestMessageBuilder extends AbstractRequestMessageBuilder<NelRequestMessage, RecognizedTextDTO>
        implements AllWindowFunction<RecognizedTextDTO, NelRequestMessage, TimeWindow> {
    private static final Logger LOG = LoggerFactory.getLogger(NelRequestMessageBuilder.class);
    private String linker;

    public NelRequestMessageBuilder() {
        super();
    }

    public NelRequestMessageBuilder(String outputTopic, String requestIdPrefix, String linker, int timeout) {
        super(outputTopic, requestIdPrefix);
        this.linker = linker;
        this.timeout = timeout;
    }

    public String getLinker() {
        return linker;
    }

    public void setLinker(String linker) {
        this.linker = linker;
    }

    @Override
    protected NelRequestMessage buildRequest(Iterable<RecognizedTextDTO> items) {
        NelRequestMessage request = new NelRequestMessage();
        this.setCommons(request);
        request.setLinker(linker);
        request.setTexts(Iterables.toArray(items, RecognizedTextDTO.class));

        return request;
    }

    @Override
    public void apply(TimeWindow window, Iterable<RecognizedTextDTO> tweets, Collector<NelRequestMessage> out) throws Exception {
        if (!tweets.iterator().hasNext()) {
            return;
        }

        NelRequestMessage request = this.buildRequest(tweets);
        LOG.debug("Starting nel processing {} tweets", request.getTexts().length);

        out.collect(request);
    }
}

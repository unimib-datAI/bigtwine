package it.unimib.disco.bigtwine.streamprocessor.request;

import com.google.common.collect.Iterables;
import it.unimib.disco.bigtwine.commons.messaging.NerRequestMessage;
import it.unimib.disco.bigtwine.commons.messaging.dto.PlainTextDTO;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class NerRequestMessageBuilder extends AbstractRequestMessageBuilder<NerRequestMessage, PlainTextDTO>
        implements AllWindowFunction<PlainTextDTO, NerRequestMessage, TimeWindow> {
    private static final Logger LOG = LoggerFactory.getLogger(NerRequestMessageBuilder.class);

    private String recognizer;

    public NerRequestMessageBuilder() {
        super();
    }

    public NerRequestMessageBuilder(String outputTopic, String requestIdPrefix, String recognizer, int timeout) {
        super(outputTopic, requestIdPrefix);
        this.recognizer = recognizer;
        this.timeout = timeout;
    }

    public String getRecognizer() {
        return recognizer;
    }

    public void setRecognizer(String recognizer) {
        this.recognizer = recognizer;
    }

    @Override
    protected NerRequestMessage buildRequest(Iterable<PlainTextDTO> tweets) {
        NerRequestMessage request = new NerRequestMessage();
        this.setCommons(request);
        request.setRecognizer(this.recognizer);
        request.setTexts(Iterables.toArray(tweets, PlainTextDTO.class));

        return request;
    }

    @Override
    public void apply(TimeWindow window, Iterable<PlainTextDTO> tweets, Collector<NerRequestMessage> out) throws Exception {
        if (!tweets.iterator().hasNext()) {
            return;
        }

        NerRequestMessage request = this.buildRequest(tweets);
        out.collect(request);

        LOG.debug("Starting ner processing {} tweets", request.getTexts().length);
    }
}

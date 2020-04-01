package it.unimib.disco.bigtwine.streamprocessor.request;

import com.google.common.collect.Iterables;
import it.unimib.disco.bigtwine.commons.messaging.RequestMessage;
import java.lang.Iterable;

import java.io.Serializable;
import java.util.*;

public abstract class AbstractRequestMessageBuilder<T extends RequestMessage, M> implements Serializable {
    protected String outputTopic;
    protected String requestIdPrefix;
    protected int maxItemsPerRequest = -1;
    protected int timeout = -1;

    public AbstractRequestMessageBuilder() {
    }

    public AbstractRequestMessageBuilder(String outputTopic, String requestIdPrefix) {
        this.outputTopic = outputTopic;
        this.requestIdPrefix = requestIdPrefix;
    }

    protected AbstractRequestMessageBuilder(String outputTopic, String requestIdPrefix, int maxItemsPerRequest, int timeout) {
        this(outputTopic, requestIdPrefix);
        this.maxItemsPerRequest = maxItemsPerRequest;
        this.timeout = timeout;
    }

    protected abstract T buildRequest(Iterable<M> items);

    protected List<T> buildRequests(Iterable<M> items) {
        if (this.maxItemsPerRequest <= 0) {
            return Collections.singletonList(this.buildRequest(items));
        }

        List<T> requests = new ArrayList<>();
        Iterable<List<M>> partitionedItems = Iterables.partition(items, maxItemsPerRequest);
        partitionedItems.forEach((partitionItems) -> requests.add(buildRequest(partitionItems)));

        return requests;
    }

    protected void setCommons(T request) {
        request.setRequestId(String.format("%s%d", this.requestIdPrefix, new Random().nextLong()));
        request.setOutputTopic(outputTopic);
        if (timeout > 0) {
            request.setExpiration(System.currentTimeMillis() + (this.timeout * 1000));
        }
    }

    public String getOutputTopic() {
        return outputTopic;
    }

    public String getRequestIdPrefix() {
        return requestIdPrefix;
    }

    public void setOutputTopic(String outputTopic) {
        this.outputTopic = outputTopic;
    }

    public void setRequestIdPrefix(String requestIdPrefix) {
        this.requestIdPrefix = requestIdPrefix;
    }

    public int getMaxItemsPerRequest() {
        return maxItemsPerRequest;
    }

    public void setMaxItemsPerRequest(int maxItemsPerRequest) {
        this.maxItemsPerRequest = maxItemsPerRequest;
    }
}

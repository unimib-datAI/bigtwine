package it.unimib.disco.bigtwine.commons.messaging;

import it.unimib.disco.bigtwine.commons.messaging.dto.LinkDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.LinkResolverExtraFieldDTO;

public class LinkResolverRequestMessage implements RequestMessage {
    private String requestId;
    private String outputTopic;
    private long timestamp;
    private long expiration;
    private LinkDTO[] links;
    private LinkResolverExtraFieldDTO[] extraFields;
    private boolean skipCache = false;

    public LinkResolverRequestMessage() {
        this.timestamp = System.currentTimeMillis();
        this.expiration = -1;
    }

    public LinkResolverRequestMessage(String requestId, LinkDTO[] links) {
        this();
        this.requestId = requestId;
        this.links = links;
    }

    public LinkResolverRequestMessage(String requestId, LinkDTO[] links, LinkResolverExtraFieldDTO[] extraFields) {
        this(requestId, links);
        this.extraFields = extraFields;
    }

    public LinkResolverRequestMessage(String requestId, LinkDTO[] links, LinkResolverExtraFieldDTO[] extraFields, long expiration) {
        this(requestId, links, extraFields);
        this.expiration = expiration;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String getOutputTopic() {
        return outputTopic;
    }

    @Override
    public void setOutputTopic(String topicId) {
        this.outputTopic = topicId;
    }

    public LinkDTO[] getLinks() {
        return links;
    }

    public void setLinks(LinkDTO[] links) {
        this.links = links;
    }

    public LinkResolverExtraFieldDTO[] getExtraFields() {
        return extraFields;
    }

    public void setExtraFields(LinkResolverExtraFieldDTO[] extraFields) {
        this.extraFields = extraFields;
    }

    public boolean isSkipCache() {
        return skipCache;
    }

    public void setSkipCache(boolean skipCache) {
        this.skipCache = skipCache;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}

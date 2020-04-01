package it.unimib.disco.bigtwine.streamprocessor.request;

import com.google.common.collect.Iterables;
import it.unimib.disco.bigtwine.commons.messaging.LinkResolverRequestMessage;
import it.unimib.disco.bigtwine.commons.messaging.dto.LinkDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.LinkResolverExtraFieldDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.LinkedEntityDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.LinkedTextDTO;
import org.apache.flink.api.common.functions.MapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LinkResolverRequestMessageBuilder extends AbstractRequestMessageBuilder<LinkResolverRequestMessage, LinkDTO>
        implements MapFunction<LinkedTextDTO, LinkResolverRequestMessage> {
    private static final Logger LOG = LoggerFactory.getLogger(LinkResolverRequestMessageBuilder.class);

    private LinkResolverExtraFieldDTO[] extraFields;

    public LinkResolverRequestMessageBuilder() {
    }

    public LinkResolverRequestMessageBuilder(String outputTopic, String requestIdPrefix) {
        super(outputTopic, requestIdPrefix);
    }

    public LinkResolverRequestMessageBuilder(String outputTopic, String requestIdPrefix, int timeout, LinkResolverExtraFieldDTO ...extraFields) {
        this(outputTopic, requestIdPrefix);
        this.timeout = timeout;
        this.extraFields = extraFields;
    }

    @Override
    protected LinkResolverRequestMessage buildRequest(Iterable<LinkDTO> items) {
        LinkResolverRequestMessage request = new LinkResolverRequestMessage();
        this.setCommons(request);
        request.setLinks(Iterables.toArray(items, LinkDTO.class));
        request.setExtraFields(this.extraFields);

        return request;
    }

    @Override
    public LinkResolverRequestMessage map(LinkedTextDTO tweet) throws Exception {
        List<LinkDTO> links = new ArrayList<>();
        for (LinkedEntityDTO entity : tweet.getEntities()) {
            if (entity.getLink() != null) {
                links.add(new LinkDTO(entity.getLink(), tweet.getTag()));
            }
        }

        LOG.debug("Starting link resolver processing {} links",links.size());

        return this.buildRequest(links);
    }
}

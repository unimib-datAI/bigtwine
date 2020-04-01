package it.unimib.disco.bigtwine.streamprocessor;

import it.unimib.disco.bigtwine.commons.messaging.dto.*;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;

import java.util.*;

class NeelProcessedTweetWindowFunction implements WindowFunction<Tuple3<String, Object, StreamType>, NeelProcessedTweetDTO, Tuple, GlobalWindow> {

    private static final Logger LOG = LoggerFactory.getLogger(NeelProcessedTweetWindowFunction.class);

    private <TI> TI deserialize(String value, Class<TI> type) {
        byte[] bytes = value.getBytes();
        return SerializationUtils.deserialize(bytes);
    }

    @Override
    public void apply(Tuple tuple, GlobalWindow window, Iterable<Tuple3<String, Object, StreamType>> input, Collector<NeelProcessedTweetDTO> out) throws Exception {
        NeelProcessedTweetDTO tweet = new NeelProcessedTweetDTO();
        TwitterStatusDTO status = new TwitterStatusDTO();
        TwitterUserDTO user = new TwitterUserDTO();
        List<LinkedEntityDTO> entities = new ArrayList<>();
        Map<String, ResourceDTO> resourcesMap = new HashMap<>();

        boolean statusReceived = false;
        // boolean entitiesReceived = false;
        // boolean resourcesReceived = false;
        // boolean locationReceived = false;

        for (Tuple3<String, Object, StreamType> t : input) {
            if (t.f2 == StreamType.status) {
                Status _status = (Status)t.f1;
                status.setId(String.valueOf(_status.getId()));
                status.setText(_status.getText());

                if (_status.getGeoLocation() != null) {
                    status.setCoordinates(new CoordinatesDTO(
                            _status.getGeoLocation().getLatitude(),
                            _status.getGeoLocation().getLongitude()));
                }

                user.setId(String.valueOf(_status.getUser().getId()));
                user.setName(_status.getUser().getName());
                user.setScreenName(_status.getUser().getScreenName());
                user.setProfileImageUrl(_status.getUser().getProfileImageURL());
                user.setLocation(_status.getUser().getLocation());
                statusReceived = true;
                LOG.debug("Received from stream StreamType.status");
            }else if (t.f2 == StreamType.linkedTweet) {
                LinkedTextDTO linkedTweet = (LinkedTextDTO)t.f1;
                entities.addAll(Arrays.asList(linkedTweet.getEntities()));
                // entitiesReceived = true;
                LOG.debug("Received from stream StreamType.linkedTweet - count {}", linkedTweet.getEntities().length);
            }else if (t.f2 == StreamType.resource) {
                @SuppressWarnings("unchecked")
                List<ResourceDTO> resources = (List<ResourceDTO>)t.f1;
                for (ResourceDTO resource : resources) {
                    resourcesMap.put(resource.getUrl(), resource);
                }
                // resourcesReceived = true;
                LOG.debug("Received from stream StreamType.resource - count {}", resources.size());
            }else if (t.f2 == StreamType.decodedLocation) {
                DecodedLocationDTO location = (DecodedLocationDTO)t.f1;
                user.setCoordinates(location.getCoordinates());
                // locationReceived = true;
                LOG.debug("Received from stream StreamType.decodedLocation");
            }
        }

        for (LinkedEntityDTO entity : entities) {
            if (!entity.isNil() && entity.getLink() != null) {
                if (resourcesMap.containsKey(entity.getLink())) {
                    entity.setResource(resourcesMap.get(entity.getLink()));
                }
            }

            if (entity.getValue() == null && entity.getPosition() != null && status.getText() != null) {
                try {
                    String value = status.getText().substring(
                            entity.getPosition().getStart(),
                            entity.getPosition().getEnd()).trim();

                    entity.setValue(value);
                } catch (StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }

        status.setUser(user);
        tweet.setStatus(status);
        tweet.setEntities(entities.toArray(new LinkedEntityDTO[0]));

        if (statusReceived) {
            LOG.debug("Collect tweet {} with {} entities", status.getId(), entities.size());
            out.collect(tweet);
        }
    }
}

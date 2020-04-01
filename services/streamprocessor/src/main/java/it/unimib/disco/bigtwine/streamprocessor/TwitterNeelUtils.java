package it.unimib.disco.bigtwine.streamprocessor;

import it.unimib.disco.bigtwine.commons.messaging.dto.LinkedEntityDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.LinkedTextDTO;
import org.apache.commons.lang.StringUtils;
import twitter4j.Status;

public class TwitterNeelUtils {
    public static boolean linkedTweetHasLinks(LinkedTextDTO tweet) {
        for (LinkedEntityDTO entity : tweet.getEntities()) {
            if (entity.getLink() != null) {
                return true;
            }
        }

        return false;
    }

    public static boolean linkedTweetHasNotLinks(LinkedTextDTO tweet) {
        return !linkedTweetHasLinks(tweet);
    }

    public static boolean statusHasUserLocation(Status status) {
        return status.getUser() != null &&
                status.getUser().getLocation() != null &&
                StringUtils.isNotBlank(status.getUser().getLocation()) &&
                status.getUser().getLocation().length() >= 2;
    }

    public static boolean statusHasNotUserLocation(Status status) {
        return !statusHasUserLocation(status);
    }
}

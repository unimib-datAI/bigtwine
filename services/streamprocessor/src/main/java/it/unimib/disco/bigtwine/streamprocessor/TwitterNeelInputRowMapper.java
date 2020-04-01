package it.unimib.disco.bigtwine.streamprocessor;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.hadoop.io.BSONWritable;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.bson.BSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitterNeelInputRowMapper implements FlatMapFunction<Tuple2<BSONWritable, BSONWritable>, TwitterNeelInputRow> {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterNeelInputRowMapper.class);

    private String serializeCoordinates(Object coords) {
        if (coords == null) {
            return null;
        }

        return coords.toString();
    }

    @Override
    public void flatMap(Tuple2<BSONWritable, BSONWritable> record, Collector<TwitterNeelInputRow> out) throws Exception {
        try {
            BSONWritable value = record.getField(1);
            BSONObject doc = value.getDoc();
            BasicDBObject payload = (BasicDBObject)doc.get("payload");
            BasicDBObject status = (BasicDBObject)payload.get("status");
            BasicDBObject user = (BasicDBObject)status.get("user");
            String tweetId = status.getString("id");

            TwitterNeelInputRow row = new TwitterNeelInputRow();
            row.setStatusId(tweetId);
            row.setStatusText(status.getString("text"));
            // row.setStatusCoordinates(serializeCoordinates(status.get("coordinates")));

            if (user != null) {
                row.setStatusUserId(user.getString("id"));
                row.setStatusUserName(user.getString("name"));
                row.setStatusUserLocation(user.getString("location"));
            }
        } catch (NullPointerException | ClassCastException e) {
            LOG.debug("Mapping error", e);
        }
    }
}

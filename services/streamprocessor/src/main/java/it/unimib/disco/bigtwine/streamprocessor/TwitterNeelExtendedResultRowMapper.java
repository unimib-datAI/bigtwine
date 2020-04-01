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

public class TwitterNeelExtendedResultRowMapper implements FlatMapFunction<Tuple2<BSONWritable, BSONWritable>, TwitterNeelExtendedResultRow> {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterNeelExtendedResultRowMapper.class);

    private String serializeCoordinates(Object coords) {
        if (coords == null) {
            return null;
        }

        return coords.toString();
    }

    @Override
    public void flatMap(Tuple2<BSONWritable, BSONWritable> record, Collector<TwitterNeelExtendedResultRow> out) throws Exception {
        try {
            BSONWritable value = record.getField(1);
            BSONObject doc = value.getDoc();
            BasicDBObject payload = (BasicDBObject)doc.get("payload");
            BasicDBObject status = (BasicDBObject)payload.get("status");
            BasicDBObject user = (BasicDBObject)status.get("user");
            BasicDBList entities = (BasicDBList)payload.get("entities");
            String tweetId = status.getString("id");

            TwitterNeelExtendedResultRow row = new TwitterNeelExtendedResultRow();
            row.setStatusId(tweetId);
            row.setStatusText(status.getString("text"));
            row.setStatusCoordinates(serializeCoordinates(status.get("coordinates")));
            row.setProcessDate(doc.get("process_date").toString());

            if (user != null) {
                row.setStatusUserId(user.getString("id"));
                row.setStatusUserName(user.getString("name"));
                row.setStatusUserLocation(user.getString("location"));
                row.setStatusUserCoordinates(serializeCoordinates(user.get("coordinates")));
            }

            if (entities.size() == 0) {
                out.collect(row);
            } else {
                for (Object entityObj: entities) {
                    BasicDBObject entity = (BasicDBObject)entityObj;
                    BasicDBObject pos = (BasicDBObject)entity.get("position");
                    BasicDBObject resource = (BasicDBObject)entity.get("resource");

                    row.setStatusId(tweetId);
                    row.setEntityPosition(String.format("%d,%d", pos.getInt("start"), pos.getInt("end")));
                    row.setEntityLink(entity.getString("link"));
                    row.setEntityNil(entity.getBoolean("isNil") ? "1" : "0");
                    row.setEntityNilCluster(entity.getString("nilCluster"));
                    row.setEntityConfidence(String.format("%.3f", entity.getDouble("confidence")));
                    row.setEntityCategory(entity.getString("category"));

                    if (resource != null) {
                        row.setEntityResourceName(resource.getString("name"));
                        row.setEntityResourceThumb(resource.getString("thumb"));
                        row.setEntityResourceCoordinates(serializeCoordinates(resource.get("coordinates")));
                        // row.setEntityResourceExtra(resource.getString("extra"));
                    }

                    out.collect(row);
                    row = new TwitterNeelExtendedResultRow();
                }
            }


        } catch (NullPointerException | ClassCastException e) {
            LOG.debug("Mapping error", e);
        }
    }
}

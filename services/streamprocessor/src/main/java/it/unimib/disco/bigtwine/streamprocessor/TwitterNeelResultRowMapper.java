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

class TwitterNeelResultRowMapper implements FlatMapFunction<Tuple2<BSONWritable, BSONWritable>, TwitterNeelResultRow> {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterNeelResultRowMapper.class);

    @Override
    public void flatMap(Tuple2<BSONWritable, BSONWritable> record, Collector<TwitterNeelResultRow> out) {
        try {
            BSONWritable value = record.getField(1);
            BSONObject doc = value.getDoc();
            BasicDBObject payload = (BasicDBObject)doc.get("payload");
            BasicDBObject status = (BasicDBObject)payload.get("status");
            BasicDBList entities = (BasicDBList)payload.get("entities");
            String tweetId = status.getString("id");

            for (Object entityObj: entities) {
                BasicDBObject entity = (BasicDBObject)entityObj;
                BasicDBObject pos = (BasicDBObject)entity.get("position");
                boolean isNil = entity.getBoolean("isNil");

                TwitterNeelResultRow row = new TwitterNeelResultRow();
                row.setTweetId(tweetId);
                row.setPositionStart(pos.getInt("start"));
                row.setPositionEnd(pos.getInt("end"));
                if (isNil) {
                    row.setResourceUri(entity.getString("nilCluster"));
                } else {
                    row.setResourceUri(entity.getString("link"));
                }
                row.setConfidence(entity.getDouble("confidence"));
                row.setCategory(entity.getString("category"));

                out.collect(row);
            }
        } catch (NullPointerException | ClassCastException e) {
            LOG.debug("Mapping error", e);
        }
    }
}

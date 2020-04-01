package it.unimib.disco.bigtwine.streamprocessor;

import com.mongodb.BasicDBObject;
import com.mongodb.hadoop.io.BSONWritable;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.bson.json.JsonWriterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

class AnalysisResultToJsonFlatMapFunction implements FlatMapFunction<Tuple2<BSONWritable, BSONWritable>, String> {
    private static final Logger LOG = LoggerFactory.getLogger(AnalysisResultToJsonFlatMapFunction.class);

    @Override
    public void flatMap(Tuple2<BSONWritable, BSONWritable> record, Collector<String> out) throws Exception {
        try {
            BSONWritable value = record.getField(1);
            BasicDBObject doc = (BasicDBObject) value.getDoc();
            BasicDBObject payload = (BasicDBObject) doc.get("payload");
            String processDate = DateTimeFormatter.ISO_INSTANT.format(doc.getDate("process_date").toInstant());

            BasicDBObject row = (BasicDBObject) payload.clone();
            row.remove("_class");
            row.append("processDate", processDate);

            JsonWriterSettings writerSettings = JsonWriterSettings.builder()
                    .indent(false)
                    .newLineCharacters("")
                    .build();
            String json = row.toJson(writerSettings);

            out.collect(json);
        } catch (Exception e) {
            LOG.error("Cannot convert record to json", e);
        }
    }
}

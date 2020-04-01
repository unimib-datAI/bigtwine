package it.unimib.disco.bigtwine.streamprocessor;

import com.google.common.collect.ImmutableMap;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.mapred.MongoInputFormat;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.hadoop.mapred.HadoopInputFormat;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.hadoop.mapred.JobConf;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class ExportResultsJob {

    private static final String TSV_FORMAT = "tsv";
    private static final String JSON_FORMAT = "json";
    private static final String TWITTER_NEEL_CHALLENGE_FORMAT = "twitter-neel-challenge";
    private static final String TWITTER_NEEL_DATASET_FORMAT = "twitter-neel-dataset";

    private static final Logger LOG = LoggerFactory.getLogger(ExportResultsJob.class);
    private static final Set<String> formats = new HashSet<>(Arrays.asList(
            TSV_FORMAT,
            JSON_FORMAT,
            TWITTER_NEEL_CHALLENGE_FORMAT,
            TWITTER_NEEL_DATASET_FORMAT
    ));
    private static final Map<String, String> formatExtensions = ImmutableMap.of(
            TSV_FORMAT, "tsv",
            JSON_FORMAT, "json",
            TWITTER_NEEL_CHALLENGE_FORMAT, "tsv",
            TWITTER_NEEL_DATASET_FORMAT, "tsv"
    );

    public static void main(String[] args) throws Exception {
        JobHeartbeatSender heartbeatSender = null;
        try {
            ParameterTool parameters = ParameterTool.fromArgs(args);

            final String jobId = parameters.getRequired("job-id");
            final int heartbeatInterval = parameters.getInt("heartbeat-interval", -1);

            if(heartbeatInterval > 0) {
                heartbeatSender = new JobHeartbeatSender(
                        Constants.KAFKA_BOOTSTRAP_SERVERS,
                        Constants.JOB_HEARTBEATS_TOPIC,
                        jobId,
                        heartbeatInterval);
            }

            launchJob(jobId, parameters, heartbeatSender);
            LOG.info("Job completed with success");

            if (heartbeatSender != null) {
                heartbeatSender.sendLast();
            }
        } catch (Exception e) {
            LOG.error("Job failed", e);

            if (heartbeatSender != null) {
                heartbeatSender.sendError(e.getLocalizedMessage());
            }
        }

        Thread.sleep(500);
    }

    private static void launchJob(String jobId, ParameterTool parameters, JobHeartbeatSender heartbeatSender) throws Exception {
        final String mongoConnectionUri = String.format("mongodb://%s:%d", Constants.MONGO_HOST, Constants.MONGO_PORT);
        final String mongoDbName = Constants.MONGO_ANALYSIS_DB;
        final String mongoCollectionName = Constants.MONGO_RESULTS_COLLECTION;
        final String gridFsConnectionUri = String.format("mongodb://%s:%d", Constants.GRIDFS_HOST, Constants.GRIDFS_PORT);
        final String gridFsDbName = Constants.GRIDFS_DB;

        final String analysisId = parameters.getRequired("analysis-id");
        final String documentId = parameters.getRequired("document-id");
        final String format = parameters.getRequired("format");
//        final String analysisId = "5d75361a2ccf47000145bcbe";
//        final String documentId = org.bson.types.ObjectId.get().toHexString();
//        System.out.println("Saving to document: " + documentId);
//        final String format = JSON_FORMAT;

        if (!formats.contains(format)) {
            throw new IllegalArgumentException(
                    String.format("%s is not a know export format, valid options are: %s", format, String.join(", ", formats))
            );
        }

        MongoClient mongoClient = MongoClients.create(mongoConnectionUri);
        MongoCollection<Document> collection = mongoClient
                .getDatabase(mongoDbName)
                .getCollection(mongoCollectionName);

        Document countDoc = collection.aggregate(Arrays.asList(
                Document.parse( "{\"$match\": {\"analysis.$id\": { \"$oid\": \""+analysisId+"\"}}}"),
                Document.parse( "{\"$unwind\": {\"path\": \"$payload.entities\"}}"),
                Document.parse( "{\"$count\": \"count\"}")
        )).first();
        long count = (countDoc != null) ? countDoc.getInteger("count") : 0;
        LOG.debug("Count: " + count);

        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        // create a MongodbInputFormat, using a Hadoop input format wrapper
        HadoopInputFormat<BSONWritable, BSONWritable> hdIf =
                new HadoopInputFormat<>(new MongoInputFormat(),
                        BSONWritable.class, BSONWritable.class,	new JobConf());

        // specify connection parameters
        hdIf.getJobConf().set("mongo.input.uri", String.format("%s/%s.%s", mongoConnectionUri, mongoDbName, mongoCollectionName));
        hdIf.getJobConf().set("mongo.input.query", "{\"analysis.$id\": { \"$oid\": \""+analysisId+"\"}}");

        LOG.debug("Query: {}", hdIf.getJobConf().get("mongo.input.query"));

        DataSource<Tuple2<BSONWritable, BSONWritable>> input = env.createInput(hdIf);

        String heading = null;
        DataSet<String> export;
        switch (format) {
            case TWITTER_NEEL_CHALLENGE_FORMAT:
                export = input
                        .flatMap(new TwitterNeelResultRowMapper())
                        .map(row -> String.join("\t",
                            row.getTweetId(),
                            row.getPositionStart().toString(),
                            row.getPositionEnd().toString(),
                            row.getResourceUri(),
                            String.valueOf(row.getConfidence()),
                            row.getCategory()
                        ));
                break;
            case TWITTER_NEEL_DATASET_FORMAT:
                ResultRowToCsvMapper<TwitterNeelInputRow> csvMapper1 = new ResultRowToCsvMapper<>();
                heading = csvMapper1.getHeading();
                export = input
                        .flatMap(new TwitterNeelInputRowMapper())
                        .map(csvMapper1);
                break;
            case JSON_FORMAT:
                export = input.flatMap(new AnalysisResultToJsonFlatMapFunction());
                break;
            case TSV_FORMAT:
                ResultRowToCsvMapper<TwitterNeelExtendedResultRow> csvMapper2 = new ResultRowToCsvMapper<>();
                heading = csvMapper2.getHeading();
                export = input
                        .flatMap(new TwitterNeelExtendedResultRowMapper())
                        .map(csvMapper2);
                break;
            default:
                return;
        }

        export.output(new GridFSOutputFormat(
                gridFsConnectionUri,
                gridFsDbName,
                documentId,
                analysisId,
                formatExtensions.get(format),
                format,
                heading,
                count,
                heartbeatSender
        )).setParallelism(1);

        env.execute();
    }

}

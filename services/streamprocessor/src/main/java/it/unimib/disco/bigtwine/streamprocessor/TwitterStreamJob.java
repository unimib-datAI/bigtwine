package it.unimib.disco.bigtwine.streamprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unimib.disco.bigtwine.commons.messaging.AnalysisResultProducedEvent;
import it.unimib.disco.bigtwine.commons.messaging.JobHeartbeatEvent;
import it.unimib.disco.bigtwine.commons.messaging.dto.*;
import it.unimib.disco.bigtwine.streamprocessor.request.GeoDecoderRequestMessageBuilder;
import it.unimib.disco.bigtwine.streamprocessor.request.LinkResolverRequestMessageBuilder;
import it.unimib.disco.bigtwine.streamprocessor.request.NelRequestMessageBuilder;
import it.unimib.disco.bigtwine.streamprocessor.request.NerRequestMessageBuilder;
import it.unimib.disco.bigtwine.streamprocessor.request.serializer.RequestMessageSerializer;
import it.unimib.disco.bigtwine.streamprocessor.response.GeoDecoderResponseMessageParser;
import it.unimib.disco.bigtwine.streamprocessor.response.LinkResolverResponseMessageParser;
import it.unimib.disco.bigtwine.streamprocessor.response.NelResponseMessageParser;
import it.unimib.disco.bigtwine.streamprocessor.response.NerResponseMessageParser;
import it.unimib.disco.bigtwine.streamprocessor.source.GridFSCsvSource;
import it.unimib.disco.bigtwine.streamprocessor.source.TimeSource;
import org.apache.commons.lang.BooleanUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;
import org.apache.flink.util.Collector;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class TwitterStreamJob {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamJob.class);

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

            launchJob(jobId, parameters);
            LOG.error("Job completed with success");

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

    private static void launchJob(String jobId, ParameterTool parameters) throws Exception {
        // Global params
        final String analysisId = parameters.getRequired("analysis-id");
        final boolean twitterSkipRetweets = parameters.getBoolean("twitter-skip-retweets", false);
        final String twitterStreamQuery = parameters.get("twitter-stream-query");
        final String twitterStreamLocations = parameters.get("twitter-stream-locations");
        final String datasetDocumentId = parameters.get("dataset-document-id");
        final int heartbeatInterval = parameters.getInt("heartbeat-interval", -1);

        final boolean isQueryInput = twitterStreamQuery != null && !StringUtils.isNullOrWhitespaceOnly(twitterStreamQuery);
        final boolean isLocationsInput = twitterStreamLocations != null && !StringUtils.isNullOrWhitespaceOnly(twitterStreamLocations);
        final boolean isDatasetInput = datasetDocumentId != null && !StringUtils.isNullOrWhitespaceOnly(datasetDocumentId);
        final boolean isStreamMode = isQueryInput || isLocationsInput;

        final int processingTimeout = parameters.getInt("processing-timeout",
                isStreamMode ? Constants.STREAM_PROCESSING_TIMEOUT : Constants.DATASET_PROCESSING_TIMEOUT);

        int providedInputCount = 0;
        providedInputCount += BooleanUtils.toInteger(isQueryInput);
        providedInputCount += BooleanUtils.toInteger(isLocationsInput);
        providedInputCount += BooleanUtils.toInteger(isDatasetInput);
        
        Preconditions.checkArgument(
                providedInputCount == 1,
                "Input not provided or multiple inputs provided" +
                "only one of {} must be provided",
                String.join(", ", new String[]{"twitter-stream-query", "twitter-stream-locations", "dataset-document-id"}));

        Preconditions.checkArgument(processingTimeout > 0, "Processing timeout must be expressed in seconds and greater then 0");

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().disableSysoutLogging();

        LOG.info(
                "Twitter Neel Job started with configuration:\n" +
                        "\tjobId = {}\n" +
                        "\tanalysisId = {}\n" +
                        "\theartbeatInterval = {}\n" +
                        "\ttwitterSkipRetweets = {}\n" +
                        "\tprocessingTimeout = {}",
                jobId, analysisId, heartbeatInterval, twitterSkipRetweets, processingTimeout
        );

        int datasetNumberOfRows = -1; // Unknown

        DataStream<Map<String, String>> datasetStream = null;
        DataStream<String> rawTweetsStream;

        if (isStreamMode) {
            final String twitterToken = parameters.getRequired("twitter-token");
            final String twitterTokenSecret = parameters.getRequired("twitter-token-secret");
            final String twitterConsumerKey = parameters.getRequired("twitter-consumer-key");
            final String twitterConsumerSecret = parameters.getRequired("twitter-consumer-secret");
            final String twitterStreamLang = parameters.get("twitter-stream-lang", "en");
            final int twitterStreamSampling = parameters.getInt("twitter-stream-sampling", -1);
            final String[] twitterStreamLangs = twitterStreamLang.split(",");

            LOG.info(
                    "Stream configuration:\n" +
                            "\ttwitterToken = {}\n" +
                            "\ttwitterTokenSecret = *****\n" +
                            "\ttwitterConsumerKey = {}\n" +
                            "\ttwitterConsumerSecret = *****\n" +
                            "\ttwitterStreamLang = {}\n" +
                            "\ttwitterStreamSampling = {}",
                    twitterToken, twitterConsumerKey, twitterStreamLang, twitterStreamSampling);

            // ----- TWITTER STREAM SOURCE
            Properties twitterProps = new Properties();
            twitterProps.setProperty(TwitterSource.CONSUMER_KEY, twitterConsumerKey);
            twitterProps.setProperty(TwitterSource.CONSUMER_SECRET, twitterConsumerSecret);
            twitterProps.setProperty(TwitterSource.TOKEN, twitterToken);
            twitterProps.setProperty(TwitterSource.TOKEN_SECRET, twitterTokenSecret);
            TwitterSource twitterSource = new TwitterSource(twitterProps);

            if (isQueryInput) {
                LOG.info("Stream query: {}", twitterStreamQuery);
                final String[] twitterStreamQueryTerms = twitterStreamQuery.split(",");
                twitterSource.setCustomEndpointInitializer(new FilterableTwitterEndpointInitializer(twitterStreamQueryTerms, twitterStreamLangs));
            } else {
                LOG.info("Stream bounding boxes: {}", twitterStreamLocations);
                twitterSource.setCustomEndpointInitializer(new FilterableTwitterEndpointInitializer(twitterStreamLocations, twitterStreamLangs));
            }

            rawTweetsStream = env
                    .addSource(twitterSource)
                    .setMaxParallelism(1)
                    .name("Tweets source");

            if (twitterStreamSampling > 0) {
                rawTweetsStream = rawTweetsStream
                        .filter(new TwitterStatusSamplingFilter(twitterStreamSampling))
                        .setMaxParallelism(1)
                        .name("Tweets sampling");

                LOG.info("Sampling enabled: {} tweets per seconds", twitterStreamSampling);
            }
        } else if (isDatasetInput) {
            LOG.info("Dataset document id: {}", datasetDocumentId);

            GridFSCsvSource gridFsSource = new GridFSCsvSource(
                    Constants.GRIDFS_HOST,
                    Constants.GRIDFS_PORT,
                    Constants.GRIDFS_DB,
                    new ObjectId(datasetDocumentId),
                    Constants.DATASET_READ_MAX_RATE);

            if (gridFsSource.getStats() != null) {
                datasetNumberOfRows = gridFsSource.getStats().getNumberOfRecords();
            }

            LOG.info("Expected dataset number of records: {}", datasetNumberOfRows);

            datasetStream = env
                    .addSource(gridFsSource)
                    .setMaxParallelism(1)
                    .name("Dataset source");

            rawTweetsStream = datasetStream
                    .map(new MapToJsonSerializer())
                    .name("Tweets source");
        } else {
            throw new RuntimeException("Missing input");
        }

        DataStream<Status> tweetsStream = rawTweetsStream
                .flatMap((String tweetJson, Collector<Status> collector) -> {
                    try {
                        Status tweet = TwitterObjectFactory.createStatus(tweetJson);
                        if (tweet.getId() > 0 && tweet.getText() != null && !tweet.getText().isEmpty()) {
                            if (!(twitterSkipRetweets && tweet.isRetweet())) {
                                collector.collect(tweet);
                            }
                        }
                    } catch (TwitterException e) {
                        LOG.debug("Tweet not parsed - {}: {}", e.getMessage(), tweetJson);
                    }
                })
                .returns(Status.class)
                .name("Tweets parsing");

        Properties kafkaConsumerProps = new Properties();
        kafkaConsumerProps.setProperty("bootstrap.servers", Constants.KAFKA_BOOTSTRAP_SERVERS);
        kafkaConsumerProps.setProperty("group.id", "streamprocessor-" + jobId);

        Properties kafkaProducerProps = new Properties();
        kafkaProducerProps.setProperty("bootstrap.servers", Constants.KAFKA_BOOTSTRAP_SERVERS);

        final String nerInputTopic = Constants.NER_INPUT_TOPIC;
        final String nerOutputTopic = String.format(Constants.NER_OUTPUT_TOPIC, analysisId);
        final String nerRecognizer = parameters.get("ner-recognizer", null);
        FlinkKafkaProducer<String> nerSink = new FlinkKafkaProducer<>(nerInputTopic, new SimpleStringSchema(), kafkaProducerProps);
        FlinkKafkaConsumer<String> nerSource = new FlinkKafkaConsumer<>(nerOutputTopic, new SimpleStringSchema(), kafkaConsumerProps);

        tweetsStream
                .map(status -> new PlainTextDTO(String.valueOf(status.getId()), status.getText()))
                .timeWindowAll(Time.seconds(3))
                .apply(new NerRequestMessageBuilder(nerOutputTopic, "ner-request-", nerRecognizer, processingTimeout))
                .map(new RequestMessageSerializer<>(nerInputTopic))
                .addSink(nerSink)
                .name("NER sink");

        DataStream<RecognizedTextDTO> recognizedTweetsStream = env
                .addSource(nerSource)
                .flatMap(new NerResponseMessageParser(nerOutputTopic));

        final String nelInputTopic = Constants.NEL_INPUT_TOPIC;
        final String nelOutputTopic = String.format(Constants.NEL_OUTPUT_TOPIC, analysisId);
        final String nelLinker = parameters.get("nel-linker", null);
        FlinkKafkaProducer<String> nelSink = new FlinkKafkaProducer<>(nelInputTopic, new SimpleStringSchema(), kafkaProducerProps);
        FlinkKafkaConsumer<String> nelSource = new FlinkKafkaConsumer<>(nelOutputTopic, new SimpleStringSchema(), kafkaConsumerProps);

        recognizedTweetsStream
                .timeWindowAll(Time.seconds(3))
                .apply(new NelRequestMessageBuilder(nelOutputTopic, "nel-request-", nelLinker, processingTimeout))
                .map(new RequestMessageSerializer<>(nelInputTopic))
                .addSink(nelSink)
                .name("NEL sink");

        DataStream<LinkedTextDTO> linkedTweetsStream = env
                .addSource(nelSource)
                .flatMap(new NelResponseMessageParser(nelOutputTopic));

        final String linkResolverInputTopic = Constants.LINKRESOLVER_INPUT_TOPIC;
        final String linkResolverOutputTopic = String.format(Constants.LINKRESOLVER_OUTPUT_TOPIC, analysisId);
        FlinkKafkaProducer<String> linkSink = new FlinkKafkaProducer<>(linkResolverInputTopic, new SimpleStringSchema(), kafkaProducerProps);
        FlinkKafkaConsumer<String> linkSource = new FlinkKafkaConsumer<>(linkResolverOutputTopic, new SimpleStringSchema(), kafkaConsumerProps);

        LinkResolverExtraFieldDTO rdfTypeExtraField = new LinkResolverExtraFieldDTO(
                "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>",
                "rdfType",
                true
        );
        linkedTweetsStream
                .filter(TwitterNeelUtils::linkedTweetHasLinks)
                .map(new LinkResolverRequestMessageBuilder(linkResolverOutputTopic, "linkresolver-request-", processingTimeout, rdfTypeExtraField))
                .map(new RequestMessageSerializer<>(linkResolverInputTopic))
                .addSink(linkSink)
                .name("Link resolver sink");

        DataStream<List<ResourceDTO>> resourcesStream = env
                .addSource(linkSource)
                .flatMap(new LinkResolverResponseMessageParser(linkResolverOutputTopic));

        final String geoDecoderInputTopic = Constants.GEODECODER_INPUT_TOPIC;
        final String geoDecoderOutputTopic = String.format(Constants.GEODECODER_OUTPUT_TOPIC, analysisId);
        final String geoDecoder = parameters.get("geo-decoder", "default");
        FlinkKafkaProducer<String> geoSink = new FlinkKafkaProducer<>(geoDecoderInputTopic, new SimpleStringSchema(), kafkaProducerProps);
        FlinkKafkaConsumer<String> geoSource = new FlinkKafkaConsumer<>(geoDecoderOutputTopic, new SimpleStringSchema(), kafkaConsumerProps);

        tweetsStream
                .filter(TwitterNeelUtils::statusHasUserLocation)
                .map(status -> new LocationDTO(status.getUser().getLocation(), String.valueOf(status.getId())))
                .timeWindowAll(Time.seconds(3))
                .apply(new GeoDecoderRequestMessageBuilder(geoDecoderOutputTopic, "geodecoder-request-", geoDecoder, 15, processingTimeout))
                .map(new RequestMessageSerializer<>(geoDecoderInputTopic))
                .addSink(geoSink)
                .name("Geo decoder sink");

        DataStream<DecodedLocationDTO> locationsStream = env
                .addSource(geoSource)
                .flatMap(new GeoDecoderResponseMessageParser(geoDecoderOutputTopic));

        DataStream<Tuple3<String, Object, StreamType>> tupleTweetsStream = tweetsStream
                .filter((tweet) -> tweet != null && tweet.getId() > 0)
                .map(tweet -> new Tuple3<>(String.valueOf(tweet.getId()), (Object)tweet, StreamType.status))
                .returns(new TypeHint<Tuple3<String, Object, StreamType>>(){})
                .name("Raw tweet tuple mapper");

        DataStream<Tuple3<String, Object, StreamType>> tupleLinkedTweetsStream = linkedTweetsStream
                .filter((tweet) -> tweet != null && tweet.getTag() != null)
                .map(linkedTweet -> new Tuple3<>(String.valueOf(linkedTweet.getTag()), (Object)linkedTweet, StreamType.linkedTweet))
                .returns(new TypeHint<Tuple3<String, Object, StreamType>>(){})
                .name("Linked tweet tuple mapper");

        DataStream<Tuple3<String, Object, StreamType>> tupleResourcesStream = resourcesStream
                .filter((resources) -> resources != null && resources.size() > 0 && resources.get(0).getTag() != null)
                .map(resources -> new Tuple3<>(resources.get(0).getTag(), (Object)resources, StreamType.resource))
                .returns(new TypeHint<Tuple3<String, Object, StreamType>>(){})
                .name("Resource tuple mapper");

        DataStream<Tuple3<String, Object, StreamType>> emptyResourcesStream = linkedTweetsStream
                .filter(TwitterNeelUtils::linkedTweetHasNotLinks)
                .map(tweet -> new Tuple3<>(String.valueOf(tweet.getTag()), (Object)new ArrayList<ResourceDTO>(), StreamType.resource))
                .returns(new TypeHint<Tuple3<String, Object, StreamType>>(){})
                .name("Empty resource tuple mapper");

        DataStream<Tuple3<String, Object, StreamType>> tupleLocationsStream = locationsStream
                .filter((loc) -> loc != null && loc.getTag() != null)
                .map(location -> new Tuple3<>(location.getTag(), (Object)location, StreamType.decodedLocation))
                .returns(new TypeHint<Tuple3<String, Object, StreamType>>(){})
                .name("Location tuple mapper");

        DataStream<Tuple3<String, Object, StreamType>> emptyLocationsStream = tweetsStream
                .filter(TwitterNeelUtils::statusHasNotUserLocation)
                .map(tweet -> new Tuple3<>(String.valueOf(tweet.getId()), (Object)new DecodedLocationDTO(null, null, String.valueOf(tweet.getId())), StreamType.decodedLocation))
                .returns(new TypeHint<Tuple3<String, Object, StreamType>>(){})
                .name("Empty location tuple mapper");

        DataStream<NeelProcessedTweetDTO> processedTweetsStream = tupleTweetsStream
                .union(tupleLinkedTweetsStream, tupleResourcesStream, emptyResourcesStream, tupleLocationsStream, emptyLocationsStream)
                .keyBy(0)
                .window(GlobalWindows.create())
                .trigger(TwitterStreamTypeWindowTrigger.create(Time.seconds(processingTimeout)))
                .apply(new NeelProcessedTweetWindowFunction())
                .name("Neel processed tweet assembler");

        FlinkKafkaProducer<String> tweetsProcessedProducer = new FlinkKafkaProducer<>("analysis-results", new SimpleStringSchema(), kafkaProducerProps);

        processedTweetsStream
                .map((tweet) -> {
                    AnalysisResultProducedEvent event = new AnalysisResultProducedEvent();
                    event.setAnalysisId(analysisId);
                    event.setProcessDate(Instant.now());
                    event.setPayload(tweet);

                    LOG.debug("Analysis result produced for tweet: {} - {}",
                            tweet.getStatus().getId(),
                            tweet.getEntities() != null ? tweet.getEntities().length : 0);

                    return event;
                })
                .map(event -> {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());
                    JsonSerializer<AnalysisResultProducedEvent> serializer = new JsonSerializer<>(mapper);

                    return new String(serializer.serialize("analysis-results", event));
                })
                .addSink(tweetsProcessedProducer)
                .name("NEEL Output Sink");

        LOG.debug("Heartbeat interval: {}", heartbeatInterval);
        if (heartbeatInterval > 0) {
            FlinkKafkaProducer<String> heartbeatSink = new FlinkKafkaProducer<>(Constants.JOB_HEARTBEATS_TOPIC, new SimpleStringSchema(), kafkaProducerProps);
            DataStream<JobHeartbeatEvent> heartbeatStream;

            if (datasetStream == null) {
                heartbeatStream = tweetsStream
                        .map(t -> 1)
                        .timeWindowAll(Time.seconds(heartbeatInterval))
                        .reduce(Integer::sum)
                        .map(count -> {
                            JobHeartbeatEvent event = new JobHeartbeatEvent();
                            event.setTimestamp(Instant.now());
                            event.setJobId(jobId);

                            return event;
                        });

            } else {

                DataStream<Tuple2<Integer, Integer>> s0 = env
                        .addSource(new TimeSource())
                        .map(ts -> new Tuple2<>(0, ts))
                        .returns(new TypeHint<Tuple2<Integer, Integer>>() {});

                DataStream<Tuple2<Integer, Integer>> s1 = datasetStream
                        .filter(Map::isEmpty)
                        .map(m -> new Tuple2<>(1, 1))
                        .returns(new TypeHint<Tuple2<Integer, Integer>>() {});

                DataStream<Tuple2<Integer, Integer>> s2 = tweetsStream
                        .map(t -> new Tuple2<>(2, 1))
                        .returns(new TypeHint<Tuple2<Integer, Integer>>() {});

                DataStream<Tuple2<Integer, Integer>> s3 = processedTweetsStream
                        .map(t -> new Tuple2<>(3, 1))
                        .returns(new TypeHint<Tuple2<Integer, Integer>>() {});


                DataStream<Tuple2<Double, Boolean>> progressStream = s0
                        .union(s1, s2, s3)
                        .windowAll(GlobalWindows.create())
                        .trigger(DatasetProgressWindowTrigger.create(Time.seconds(heartbeatInterval)))
                        .apply(DatasetProgressWindowFunction.create(Time.seconds(processingTimeout + 5), datasetNumberOfRows))
                        .setMaxParallelism(1);

                heartbeatStream = progressStream
                        .map(t -> {
                            JobHeartbeatEvent event = new JobHeartbeatEvent();
                            event.setTimestamp(Instant.now());
                            event.setJobId(jobId);
                            event.setProgress(t.f0);
                            event.setLast(t.f1);
                            LOG.debug("Sending heartbeat with progress: {}, last: {}", t.f0, t.f1);

                            return event;
                        });
            }

            heartbeatStream
                    .map(event -> {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        JsonSerializer<JobHeartbeatEvent> serializer = new JsonSerializer<>(mapper);

                        return new String(serializer.serialize(Constants.JOB_HEARTBEATS_TOPIC, event));
                    })
                    .addSink(heartbeatSink);
        }

        env.execute();
    }

}

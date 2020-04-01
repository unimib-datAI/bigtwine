package it.unimib.disco.bigtwine.streamprocessor;

public class Constants {
    public static final String KAFKA_BOOTSTRAP_SERVERS = getEnv("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092");

    public static final String GRIDFS_HOST = getEnv("GRIDFS_HOST", "mongodb");
    public static final int GRIDFS_PORT = getEnv("GRIDFS_PORT", 27017);
    public static final String GRIDFS_DB = getEnv("GRIDFS_DB", "analysis");

    public static final String MONGO_HOST = getEnv("MONGO_HOST", "mongodb");
    public static final int MONGO_PORT = getEnv("MONGO_PORT", 27017);
    public static final String MONGO_ANALYSIS_DB = getEnv("MONGO_ANALYSIS_DB", "analysis");
    public static final String MONGO_RESULTS_COLLECTION = getEnv("MONGO_RESULTS_COLLECTION", "analyses.results");

    public static final String NER_INPUT_TOPIC = getEnv("NER_INPUT_TOPIC", "ner-requests");
    public static final String NER_OUTPUT_TOPIC = getEnv("NER_OUTPUT_TOPIC", "ner-responses.%s");

    public static final String NEL_INPUT_TOPIC = getEnv("NEL_INPUT_TOPIC", "nel-requests");
    public static final String NEL_OUTPUT_TOPIC = getEnv("NEL_OUTPUT_TOPIC", "nel-responses.%s");

    public static final String LINKRESOLVER_INPUT_TOPIC = getEnv("LINKRESOLVER_INPUT_TOPIC", "linkresolver-requests");
    public static final String LINKRESOLVER_OUTPUT_TOPIC = getEnv("LINKRESOLVER_OUTPUT_TOPIC", "linkresolver-responses.%s");

    public static final String GEODECODER_INPUT_TOPIC = getEnv("GEODECODER_INPUT_TOPIC", "geodecoder-requests");
    public static final String GEODECODER_OUTPUT_TOPIC = getEnv("GEODECODER_OUTPUT_TOPIC", "geodecoder-responses.%s");

    public static final String JOB_HEARTBEATS_TOPIC = getEnv("JOB_HEARTBEATS_TOPIC", "job-heartbeats");

    /** Max number of seconds to process a tweet from a stream */
    public static final int STREAM_PROCESSING_TIMEOUT = getEnv("STREAM_PROCESSING_TIMEOUT", 15);

    /** Max number of seconds to process a tweet from a dataset */
    public static final int DATASET_PROCESSING_TIMEOUT = getEnv("DATASET_PROCESSING_TIMEOUT", 30);

    /** Max number of rows per seconds read from a dataset */
    public static final int DATASET_READ_MAX_RATE = getEnv("DATASET_READ_MAX_RATE", 4);

    public static String getEnv(String name, String fallback) {
        String value = System.getenv(name);
        return value != null ? value : fallback;
    }

    public static int getEnv(String name, int fallback) {
        String value = System.getenv(name);
        return value != null ? Integer.valueOf(value) : fallback;
    }
}

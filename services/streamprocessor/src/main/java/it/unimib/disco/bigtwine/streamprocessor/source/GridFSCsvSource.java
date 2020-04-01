package it.unimib.disco.bigtwine.streamprocessor.source;

import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GridFSCsvSource implements SourceFunction<Map<String, String>> {
    private final int STATS_BYTES_COUNT = 2097152;

    private final String mongoHost;
    private final int mongoPort;
    private final String dbName;
    private final ObjectId objectId;
    private final int maxReadRate;
    private char fieldDelimiter = '\t';
    private char lineDelimiter = '\n';
    private boolean cancelled = false;

    private transient Stats stats = null;
    private transient GridFS fs;

    public GridFSCsvSource(String mongoHost, int mongoPort, String dbName, ObjectId objectId, int maxReadRate) {
        this.mongoHost = mongoHost;
        this.mongoPort = mongoPort;
        this.dbName = dbName;
        this.objectId = objectId;
        this.maxReadRate = maxReadRate;
    }

    private GridFS getFs() {
        if (this.fs == null) {
            MongoClient mongoClient = new MongoClient(mongoHost, mongoPort);
            this.fs = new GridFS(mongoClient.getDB(this.dbName));
        }

        return this.fs;
    }

    private GridFSDBFile getGridFSDBFile() {
        return this.getFs().findOne(this.objectId);
    }

    private Reader getFileReader() {
        GridFSDBFile file = this.getGridFSDBFile();
        return new InputStreamReader(file.getInputStream());
    }

    private CSVParser getCSVParser(Reader reader) throws IOException {
        return CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withDelimiter(fieldDelimiter)
                .withRecordSeparator(lineDelimiter)
                .parse(reader);
    }

    private void calculateStats() {
        try {
            GridFSDBFile file = this.getGridFSDBFile();
            Reader fileReader = new InputStreamReader(file.getInputStream());
            char[] buf = new char[STATS_BYTES_COUNT];
            int readLen = fileReader.read(buf, 0, STATS_BYTES_COUNT);

            long fileSize = file.getLength();
            int expNumberOfRecords = 0;
            int avgRecordSize = 0;
            if (readLen > 0) {
                CharArrayReader bufReader = new CharArrayReader(buf);
                CSVParser parser = this.getCSVParser(bufReader);
                int numberOfRecords = parser.getRecords().size();

                if (numberOfRecords > 0) {
                    avgRecordSize = (int) (readLen / (double) numberOfRecords);
                }

                if (fileSize > readLen) {
                    expNumberOfRecords = (int)((fileSize / (double) readLen) * numberOfRecords);
                } else {
                    expNumberOfRecords = numberOfRecords;
                }
            }

            this.stats = new Stats(fileSize, avgRecordSize, expNumberOfRecords);
        } catch (IOException e) {
            this.stats = null;
        }
    }

    @Override
    public void run(SourceContext<Map<String, String>> ctx) throws Exception {
        Reader reader = this.getFileReader();
        CSVParser parser = this.getCSVParser(reader);
        Iterator<CSVRecord> records = parser.iterator();

        while (records.hasNext()) {
            if (cancelled) {
                break;
            }

            CSVRecord record = records.next();
            Map<String, String> recordMap = record.toMap();

            if (recordMap.size() > 0) {
                ctx.collect(recordMap);
            }

            if (this.maxReadRate > 0) {
                ctx.markAsTemporarilyIdle();
                Thread.sleep(1000 / this.maxReadRate);
            }
        }

        ctx.collect(new HashMap<>());
        ctx.markAsTemporarilyIdle();

        while (!cancelled) {
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    public GridFSCsvSource lineDelimiter(final char lineDelimiter) {
        this.lineDelimiter = lineDelimiter;
        return this;
    }

    public GridFSCsvSource fieldDelimiter(final char fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
        return this;
    }

    public Stats getStats() {
        if (this.stats == null) {
            this.calculateStats();
        }

        return this.stats;
    }

    public static class Stats {
        private final long fileLength;
        private final int avgRecordLength;
        private final int numberOfRecords;

        public Stats(long fileLength, int avgRecordLength, int numberOfRecords) {
            this.fileLength = fileLength;
            this.avgRecordLength = avgRecordLength;
            this.numberOfRecords = numberOfRecords;
        }

        public long getFileLength() {
            return fileLength;
        }

        public int getAvgRecordLength() {
            return avgRecordLength;
        }

        public int getNumberOfRecords() {
            return numberOfRecords;
        }
    }
}

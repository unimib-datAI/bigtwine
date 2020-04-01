package it.unimib.disco.bigtwine.commons.csv.adapters;

import it.unimib.disco.bigtwine.commons.csv.CSVReader;
import it.unimib.disco.bigtwine.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ApacheCsvParserAdapter implements CSVReader {

    private CSVParser csvParser;

    public ApacheCsvParserAdapter(CSVParser csvParser) {
        this.csvParser = csvParser;
    }

    @Override
    public long getRecordNumber() {
        return this.csvParser.getRecordNumber();
    }

    @Override
    public List<CSVRecord> getRecords() throws IOException {
        List<CSVRecord> records = new ArrayList<>();
        for (org.apache.commons.csv.CSVRecord record : this.csvParser.getRecords()) {
            records.add(new ApacheCsvRecordAdapter(record));
        }
        return records;
    }

    @Override
    public void close() throws IOException {
        this.csvParser.close();
    }

    @Override
    public Iterator<CSVRecord> iterator() {
        return new Iterator<CSVRecord>() {

            @Override
            public boolean hasNext() {
                return csvParser.iterator().hasNext();
            }

            @Override
            public CSVRecord next() {
                return new ApacheCsvRecordAdapter(csvParser.iterator().next());
            }

            @Override
            public void remove() {
                csvParser.iterator().remove();
            }
        };
    }
}

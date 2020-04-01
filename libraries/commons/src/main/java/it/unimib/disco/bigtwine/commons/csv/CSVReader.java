package it.unimib.disco.bigtwine.commons.csv;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface CSVReader extends Iterable<CSVRecord>, Closeable {
    long getRecordNumber();
    List<CSVRecord> getRecords() throws IOException;
}

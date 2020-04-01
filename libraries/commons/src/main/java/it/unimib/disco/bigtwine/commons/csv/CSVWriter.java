package it.unimib.disco.bigtwine.commons.csv;

import java.io.IOException;

public interface CSVWriter {
    void writeRecord(final Object... values) throws IOException;
}

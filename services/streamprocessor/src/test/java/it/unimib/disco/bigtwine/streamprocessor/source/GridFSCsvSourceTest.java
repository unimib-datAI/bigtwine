package it.unimib.disco.bigtwine.streamprocessor.source;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;

public class GridFSCsvSourceTest {

    private final int STATS_BYTES_COUNT = 2097152;
    private char fieldDelimiter = '\t';
    private char lineDelimiter = '\n';

    private Reader getFileReader() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/tweets-small.csv");
        assertNotNull(inputStream);
        String csv = IOUtils.toString(inputStream, "UTF-8");
        assertNotNull(csv);

        return new StringReader(csv);
    }

    private CSVParser getCSVParser(Reader reader) throws Exception {
        return CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withDelimiter(fieldDelimiter)
                .withRecordSeparator(lineDelimiter)
                .parse(reader);
    }

    @Test
    public void testCSVParser() throws Exception {
        Reader fileReader = this.getFileReader();
        char[] buf = new char[STATS_BYTES_COUNT];
        int r = fileReader.read(buf, 0, STATS_BYTES_COUNT);

        CharArrayReader bufReader = new CharArrayReader(buf);
        CSVParser parser = this.getCSVParser(bufReader);
        int numberOfRecords = parser.getRecords().size();


        System.out.println("Records: " + numberOfRecords);

        assertTrue(numberOfRecords > 0);
    }
}

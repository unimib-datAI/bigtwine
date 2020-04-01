package it.unimib.disco.bigtwine.streamprocessor;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.junit.Test;

public class ExportResultsJobTest {

    @Test
    public void csvColumnsName() throws Exception {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper
                .schemaFor(TwitterNeelExtendedResultRow.class)
                .withColumnSeparator('\t')
                .withHeader();

        String csvHeader = csvMapper
                .writerFor(TwitterNeelExtendedResultRow[].class)
                .with(csvSchema)
                .writeValueAsString(new TwitterNeelExtendedResultRow[0]);

        System.out.println(csvHeader);
    }
}

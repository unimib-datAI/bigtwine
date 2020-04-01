package it.unimib.disco.bigtwine.streamprocessor;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.flink.api.common.functions.MapFunction;

public class ResultRowToCsvMapper<IN> implements MapFunction<IN, String> {

    private transient CsvSchema csvSchema;
    private transient CsvMapper csvMapper;
    private transient ObjectWriter objectWriter;

    private void configure() {
        this.csvMapper = new CsvMapper();
        this.csvSchema = csvMapper
                .schemaFor(TwitterNeelExtendedResultRow.class)
                .withColumnSeparator('\t')
                .withLineSeparator("")
                .withoutHeader();
        this.objectWriter = this.csvMapper
                .writerFor(TwitterNeelExtendedResultRow.class)
                .with(csvSchema);
    }

    private CsvSchema getCsvSchema() {
        if (this.csvSchema == null) {
            this.configure();
        }

        return this.csvSchema;
    }

    private  CsvMapper getCsvMapper() {
        if (this.csvMapper == null) {
            this.configure();
        }

        return this.csvMapper;
    }

    private  ObjectWriter getObjectWriter() {
        if (this.objectWriter == null) {
            this.configure();
        }

        return this.objectWriter;
    }

    public String getHeading() throws Exception {
        return this.getCsvMapper()
                .writerFor(TwitterNeelExtendedResultRow.class)
                .with(this.getCsvSchema().withHeader())
                .writeValueAsString(null);
    }

    @Override
    public String map(IN value) throws Exception {
        return this.getObjectWriter().writeValueAsString(value);
    }
}

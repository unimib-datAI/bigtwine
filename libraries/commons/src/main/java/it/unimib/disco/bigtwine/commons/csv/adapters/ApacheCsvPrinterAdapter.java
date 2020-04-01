package it.unimib.disco.bigtwine.commons.csv.adapters;

import it.unimib.disco.bigtwine.commons.csv.CSVWriter;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;

public class ApacheCsvPrinterAdapter implements CSVWriter {

    private CSVPrinter csvPrinter;

    public ApacheCsvPrinterAdapter(CSVPrinter csvPrinter) {
        this.csvPrinter = csvPrinter;
    }

    @Override
    public void writeRecord(Object... values) throws IOException {
        this.csvPrinter.printRecord(values);
    }
}

package it.unimib.disco.bigtwine.services.ner.producers;

import it.unimib.disco.bigtwine.commons.csv.CSVFactory;
import it.unimib.disco.bigtwine.commons.csv.CSVWriter;
import it.unimib.disco.bigtwine.services.ner.domain.PlainText;

import java.io.*;


final public class RitterInputProducer implements InputProducer {
    static private final char columnDelimiter = '\t';
    private CSVFactory csvFactory;
    private Writer writer;
    private BufferedWriter buffer;
    private CSVWriter csvWriter;

    public RitterInputProducer(CSVFactory csvFactory) {
        this.csvFactory = csvFactory;
    }

    @Override
    public void setWriter(Writer writer) throws IOException {
        this.writer = writer;
        this.buffer = new BufferedWriter(writer);
        this.csvWriter = this.csvFactory.getWriter(this.buffer, columnDelimiter);
    }

    @Override
    public Writer getWriter() {
        return this.writer;
    }

    @Override
    public void append(PlainText tweet) throws IOException {
        if (this.buffer == null) throw new AssertionError("A writer was not set");
        this.csvWriter.writeRecord(tweet.getTag(), tweet.getText());
    }

    @Override
    public String toString() {
        try {
            this.close();
        }catch (IOException e) {
            return null;
        }

        return this.buffer.toString();
    }

    @Override
    public void close() throws IOException {
        if (this.buffer == null) throw new AssertionError("A writer was not set");
        this.buffer.close();
    }

    @Override
    public void flush() throws IOException {
        if (this.buffer == null) throw new AssertionError("A writer was not set");
        this.buffer.flush();
    }
}

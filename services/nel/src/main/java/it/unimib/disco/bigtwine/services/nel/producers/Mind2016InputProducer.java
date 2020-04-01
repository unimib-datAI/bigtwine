package it.unimib.disco.bigtwine.services.nel.producers;

import it.unimib.disco.bigtwine.services.nel.domain.NamedEntity;
import it.unimib.disco.bigtwine.services.nel.domain.RecognizedText;

import java.io.IOException;
import java.io.Writer;

public final class Mind2016InputProducer implements InputProducer {

    private Writer writer;
    private static final String lineDelimiter = "\n";
    private static final String colDelimiter = "\t";

    @Override
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public Writer getWriter() {
        return this.writer;
    }

    @Override
    public void append(RecognizedText item) throws IOException {
        this.writer.append(String.join(colDelimiter, "[#ID#]", item.getTag()));
        this.writer.append(lineDelimiter);
        for (NamedEntity entity : item.getEntities()) {

            this.writer.append(String.join(colDelimiter,
                "[#ETS#]",
                entity.getValue(),
                entity.getLabel(),
                Float.toString(entity.getProbability())));
            this.writer.append(lineDelimiter);

        }
        this.writer.append(String.join(colDelimiter, "[#TWEET#]", item.getText()));
        this.writer.append(lineDelimiter);
        this.writer.append(lineDelimiter);
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }

    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }
}

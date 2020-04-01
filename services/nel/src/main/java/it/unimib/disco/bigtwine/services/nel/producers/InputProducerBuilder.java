package it.unimib.disco.bigtwine.services.nel.producers;

import it.unimib.disco.bigtwine.services.nel.Linker;

import java.io.IOException;
import java.io.Writer;

public class InputProducerBuilder {
    private Linker linker;

    private Writer writer;

    public static InputProducerBuilder getDefaultBuilder() {
        return new InputProducerBuilder();
    }

    public InputProducerBuilder setLinker(Linker linker) {
        this.linker = linker;
        return this;
    }

    public Linker getLinker() {
        return linker;
    }

    public InputProducerBuilder setWriter(Writer writer) {
        this.writer = writer;
        return this;
    }

    public Writer getWriter() {
        return writer;
    }

    public InputProducer build() {
        if (this.linker == null) {
            return null;
        }

        if (this.writer == null) {
            return null;
        }

        InputProducer inputProducer;
        switch (this.linker) {
            case mind2016:
                inputProducer = new Mind2016InputProducer();
                break;
            default:
                return null;
        }

        try {
            inputProducer.setWriter(this.writer);
        } catch (IOException e) {
            return null;
        }

        return inputProducer;
    }
}

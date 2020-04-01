package it.unimib.disco.bigtwine.commons.producers;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface GenericInputProducer<E> extends Closeable, Flushable {

    void setWriter(Writer writer) throws IOException;
    Writer getWriter();
    String toString();

    void append(E item) throws IOException;

    default void append(List<E> items) throws IOException {
        for (E item : items) {
            this.append(item);
        }
    }

    default void append(E[] items) throws IOException {
        for (E item : items) {
            this.append(item);
        }
    }
}

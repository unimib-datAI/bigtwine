package it.unimib.disco.bigtwine.commons.parsers;

import java.io.Reader;
import java.util.Iterator;

public interface GenericOutputParser<E> extends Iterator<E> {
    Reader getReader();
    void setReader(Reader reader);
    E[] items();
}

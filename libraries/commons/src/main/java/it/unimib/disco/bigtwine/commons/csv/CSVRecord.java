package it.unimib.disco.bigtwine.commons.csv;

public interface CSVRecord extends Iterable<String> {
    String get(final int i);
    String get(final String name);
    int size();
}

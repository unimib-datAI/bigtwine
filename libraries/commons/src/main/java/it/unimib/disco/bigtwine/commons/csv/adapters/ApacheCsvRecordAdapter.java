package it.unimib.disco.bigtwine.commons.csv.adapters;


import it.unimib.disco.bigtwine.commons.csv.CSVRecord;

import javax.validation.constraints.NotNull;
import java.util.Iterator;

public class ApacheCsvRecordAdapter implements CSVRecord {

    private org.apache.commons.csv.CSVRecord record;

    public ApacheCsvRecordAdapter(org.apache.commons.csv.CSVRecord record) {
        this.record = record;
    }

    @Override
    public String get(final int i) {
        return this.record.get(i);
    }

    @Override
    public String get(final String name) {
        return this.record.get(name);
    }

    @Override
    public int size() {
        return this.record.size();
    }

    @Override
    public Iterator<String> iterator() {
        return this.record.iterator();
    }

}

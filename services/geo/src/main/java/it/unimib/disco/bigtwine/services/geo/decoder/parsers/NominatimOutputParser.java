package it.unimib.disco.bigtwine.services.geo.decoder.parsers;

import it.unimib.disco.bigtwine.services.geo.domain.DecodedLocation;

import java.io.Reader;

public final class NominatimOutputParser implements OutputParser {
    @Override
    public Reader getReader() {
        return null;
    }

    @Override
    public void setReader(Reader reader) {

    }

    @Override
    public DecodedLocation[] items() {
        return new DecodedLocation[0];
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public DecodedLocation next() {
        return null;
    }
}

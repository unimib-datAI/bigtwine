package it.unimib.disco.bigtwine.services.geo.decoder;

import javax.validation.constraints.NotNull;

public enum Decoder {
    nominatim;

    private static Decoder defaultDecoder = nominatim;

    public static Decoder getDefault() {
        return defaultDecoder;
    }
    public static void setDefault(@NotNull Decoder decoder) {
        defaultDecoder = decoder;
    }
}

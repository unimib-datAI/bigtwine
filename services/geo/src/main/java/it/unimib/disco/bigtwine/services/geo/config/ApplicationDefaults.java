package it.unimib.disco.bigtwine.services.geo.config;

public interface ApplicationDefaults {
    String defaultDecoder = "nominatim";

    interface Executors {
        interface Nominatim {
            String apiEmail = null;
        }
    }
}

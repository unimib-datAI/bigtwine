package it.unimib.disco.bigtwine.services.geo.config;

import it.unimib.disco.bigtwine.services.geo.decoder.Decoder;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Geo.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private String defaultDecoder = ApplicationDefaults.defaultDecoder;
    private final Executors executors = new Executors();
    private final Processors processors = new Processors();

    public String getDefaultDecoder() {
        return defaultDecoder;
    }

    public void setDefaultDecoder(String defaultDecoder) {
        this.defaultDecoder = defaultDecoder;
        Decoder decoder = Decoder.valueOf(defaultDecoder);
        Decoder.setDefault(decoder);
    }

    public Executors getExecutors() {
        return this.executors;
    }

    public Processors getProcessors() {
        return this.processors;
    }

    public static class Processors {

    }

    public static class Executors {
        private final Nominatim nominatim = new Nominatim();

        public Nominatim getNominatim() {
            return nominatim;
        }

        public static class Nominatim {
            private String apiEmail = ApplicationDefaults.Executors.Nominatim.apiEmail;

            public String getApiEmail() {
                return apiEmail;
            }

            public void setApiEmail(String apiEmail) {
                this.apiEmail = apiEmail;
            }
        }
    }
}

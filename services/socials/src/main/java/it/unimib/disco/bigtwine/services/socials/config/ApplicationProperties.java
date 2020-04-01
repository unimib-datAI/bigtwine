package it.unimib.disco.bigtwine.services.socials.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Socials.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private final Security security = new Security();

    public Security getSecurity() {
        return security;
    }

    public static class Security {
        private final Encryptors encryptors = new Encryptors();
        private final Twitter twitter = new Twitter();

        public Encryptors getEncryptors() {
            return encryptors;
        }

        public Twitter getTwitter() {
            return twitter;
        }

        public static class Encryptors {
            private String secret;
            private String salt;

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }

            public String getSalt() {
                return salt;
            }

            public void setSalt(String salt) {
                this.salt = salt;
            }
        }

        public static class Twitter {
            private String consumerKey;
            private String consumerSecret;

            public String getConsumerKey() {
                return consumerKey;
            }

            public void setConsumerKey(String consumerKey) {
                this.consumerKey = consumerKey;
            }

            public String getConsumerSecret() {
                return consumerSecret;
            }

            public void setConsumerSecret(String consumerSecret) {
                this.consumerSecret = consumerSecret;
            }
        }
    }
}

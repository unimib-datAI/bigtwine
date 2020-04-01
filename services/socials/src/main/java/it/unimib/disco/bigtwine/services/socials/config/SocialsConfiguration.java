package it.unimib.disco.bigtwine.services.socials.config;

import com.mongodb.MongoClient;
import it.unimib.disco.bigtwine.services.socials.connect.AccountCreatorLocator;
import it.unimib.disco.bigtwine.services.socials.connect.AccountCreatorRegistry;
import org.eluder.spring.social.mongodb.MongoConnectionTransformers;
import org.eluder.spring.social.mongodb.MongoUsersConnectionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

@Configuration
public class SocialsConfiguration {

    private final ApplicationProperties applicationProperties;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    public SocialsConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator() {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();

        registry.addConnectionFactory(new TwitterConnectionFactory(
            this.applicationProperties.getSecurity().getTwitter().getConsumerKey(),
            this.applicationProperties.getSecurity().getTwitter().getConsumerSecret()));

        return registry;
    }

    @Bean
    public UsersConnectionRepository getUserConnectionRepository(
        MongoClient mongo,
        ConnectionFactoryLocator connectionFactoryLocator,
        TextEncryptor textEncryptor) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongo, databaseName);
        MongoConnectionTransformers transformers = new MongoConnectionTransformers(connectionFactoryLocator, textEncryptor);
        return new MongoUsersConnectionRepository(mongoTemplate, connectionFactoryLocator, transformers);
    }

    @Bean
    public AccountCreatorLocator getAccountCreatorLocator() {
        return new AccountCreatorRegistry();
    }

}

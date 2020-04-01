package it.unimib.disco.bigtwine.services.socials.config.dbmigrations;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import org.eluder.spring.social.mongodb.MongoConnection;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@ChangeLog(order = "001")
public class DemoUsersMigration {
    @ChangeSet(order = "01", author = "initiator", id = "01-addDemoUsers")
    public void addDemoUsers(MongoTemplate mongoTemplate, Environment env) {
        String twitterUserId = env.getProperty("application.security.twitter.demo-user.id");
        String twitterUserName = env.getProperty("application.security.twitter.demo-user.name");
        String accessToken = env.getProperty("application.security.twitter.demo-user.access-token");
        String accessSecret = env.getProperty("application.security.twitter.demo-user.access-secret");

        assert (accessToken != null && accessSecret != null && twitterUserId != null && twitterUserName != null) : "Missing data for demo user";

        TextEncryptor encryptor = getTextEncryptor(env);

        MongoConnection demoConnection = new MongoConnection();
        demoConnection.setUserId("demo");
        demoConnection.setProviderId("twitter");
        demoConnection.setProviderUserId(twitterUserId);
        demoConnection.setDisplayName("@" + twitterUserName);
        demoConnection.setAccessToken(encryptor.encrypt(accessToken));
        demoConnection.setSecret(encryptor.encrypt(accessSecret));
        demoConnection.setProfileUrl("http://twitter.com/" + twitterUserName);
        demoConnection.setImageUrl("http://abs.twimg.com/sticky/default_profile_images/default_profile_normal.png");

        mongoTemplate.save(demoConnection);
    }

    private TextEncryptor getTextEncryptor(Environment env) {
        String secret = env.getProperty("application.security.encryptors.secret");
        String salt = env.getProperty("application.security.encryptors.salt");

        assert (secret != null && salt != null) : "Encryptor secret or salt missing";

        return Encryptors.text(secret, salt);
    }
}

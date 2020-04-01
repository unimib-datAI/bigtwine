package it.unimib.disco.bigtwine.services.apigateway.config.dbmigrations;

import it.unimib.disco.bigtwine.services.apigateway.domain.Authority;
import it.unimib.disco.bigtwine.services.apigateway.domain.User;
import it.unimib.disco.bigtwine.services.apigateway.security.AuthoritiesConstants;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;

/**
 * Creates the initial database setup
 */
@ChangeLog(order = "001")
public class InitialSetupMigration {

    @ChangeSet(order = "01", author = "initiator", id = "01-addAuthorities")
    public void addAuthorities(MongoTemplate mongoTemplate) {
        Authority adminAuthority = new Authority();
        adminAuthority.setName(AuthoritiesConstants.ADMIN);
        Authority demoAuthority = new Authority();
        demoAuthority.setName(AuthoritiesConstants.DEMO);
        Authority userAuthority = new Authority();
        userAuthority.setName(AuthoritiesConstants.USER);
        Authority userProAuthority = new Authority();
        userProAuthority.setName(AuthoritiesConstants.USER_PRO);
        Authority userSocialAuthority = new Authority();
        userSocialAuthority.setName(AuthoritiesConstants.USER_SOCIAL);

        mongoTemplate.save(adminAuthority);
        mongoTemplate.save(demoAuthority);
        mongoTemplate.save(userAuthority);
        mongoTemplate.save(userProAuthority);
        mongoTemplate.save(userSocialAuthority);
    }

    @ChangeSet(order = "02", author = "initiator", id = "02-addUsers")
    public void addUsers(MongoTemplate mongoTemplate) {
        Authority adminAuthority = new Authority();
        adminAuthority.setName(AuthoritiesConstants.ADMIN);
        Authority demoAuthority = new Authority();
        demoAuthority.setName(AuthoritiesConstants.DEMO);
        Authority userAuthority = new Authority();
        userAuthority.setName(AuthoritiesConstants.USER);

        User systemUser = new User();
        systemUser.setId("user-0");
        systemUser.setLogin("system");
        systemUser.setPassword("$2a$10$mE.qmcV0mFU5NcKh73TZx.z4ueI/.bDWbj0T1BYyqP481kGGarKLG");
        systemUser.setFirstName("");
        systemUser.setLastName("System");
        systemUser.setEmail("system@bigtwine");
        systemUser.setActivated(true);
        systemUser.setLangKey("en");
        systemUser.setCreatedBy(systemUser.getLogin());
        systemUser.setCreatedDate(Instant.now());
        systemUser.getAuthorities().add(adminAuthority);
        systemUser.getAuthorities().add(userAuthority);
        mongoTemplate.save(systemUser);

        User anonymousUser = new User();
        anonymousUser.setId("user-1");
        anonymousUser.setLogin("anonymoususer");
        anonymousUser.setPassword("$2a$10$j8S5d7Sr7.8VTOYNviDPOeWX8KcYILUVJBsYV83Y5NtECayypx9lO");
        anonymousUser.setFirstName("Anonymous");
        anonymousUser.setLastName("User");
        anonymousUser.setEmail("anonymous@bigtwine");
        anonymousUser.setActivated(true);
        anonymousUser.setLangKey("en");
        anonymousUser.setCreatedBy(systemUser.getLogin());
        anonymousUser.setCreatedDate(Instant.now());
        mongoTemplate.save(anonymousUser);

        User adminUser = new User();
        adminUser.setId("user-2");
        adminUser.setLogin("admin");
        adminUser.setPassword("$2a$10$3epRKTp8ccIa.D0W6FVjaONcIh0tMd4tVraaDSkiRB6S4zM74YHXG"); // B1gtw1n3!
        adminUser.setFirstName("admin");
        adminUser.setLastName("Administrator");
        adminUser.setEmail("admin@bigtwine");
        adminUser.setActivated(true);
        adminUser.setLangKey("en");
        adminUser.setCreatedBy(systemUser.getLogin());
        adminUser.setCreatedDate(Instant.now());
        adminUser.getAuthorities().add(adminAuthority);
        adminUser.getAuthorities().add(userAuthority);
        mongoTemplate.save(adminUser);

        User demoUser = new User();
        demoUser.setId("demo");
        demoUser.setLogin("demo");
        demoUser.setPassword("$2a$10$/ZpS8vDqQFpMYdtLF7DUCOL0sfsjmglU9FZA.568EQguyK/9x9rjy"); // demo
        demoUser.setFirstName("");
        demoUser.setLastName("Demo");
        demoUser.setEmail("demo@bigtwine");
        demoUser.setActivated(true);
        demoUser.setLangKey("en");
        demoUser.setCreatedBy(systemUser.getLogin());
        demoUser.setCreatedDate(Instant.now());
        demoUser.getAuthorities().add(demoAuthority);
        demoUser.getAuthorities().add(userAuthority);
        mongoTemplate.save(demoUser);
    }
}

package it.unimib.disco.bigtwine.services.socials.connect;

import it.unimib.disco.bigtwine.services.socials.connect.support.FullNameSplitter;
import it.unimib.disco.bigtwine.services.socials.connect.support.NameComponents;
import it.unimib.disco.bigtwine.services.socials.domain.Account;
import org.springframework.social.connect.Connection;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.web.client.RestOperations;

public final class TwitterAccountCreator implements AccountCreator<Twitter> {

    private final static String VERIFY_CREDENTIALS_URL = "https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true";

    private final FullNameSplitter fullNameSplitter;

    public TwitterAccountCreator(FullNameSplitter fullNameSplitter) {
        this.fullNameSplitter = fullNameSplitter;
    }

    @Override
    public Account execute(Connection<Twitter> connection) throws Exception {
        RestOperations rest = connection.getApi().restOperations();
        TwitterProfile profile = rest.getForObject(VERIFY_CREDENTIALS_URL, TwitterProfile.class);

        if (profile == null) {
            return null;
        }

        NameComponents name = this.fullNameSplitter.split(profile.getName());
        String email = (String)profile.getExtraData().get("email");

        return new Account()
            .login(profile.getScreenName())
            .firstName(name.getFirstName())
            .lastName(name.getLastName())
            .email(email)
            .imageUrl(profile.getProfileImageUrl());
    }
}

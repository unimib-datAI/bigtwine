package it.unimib.disco.bigtwine.services.socials.connect;

import it.unimib.disco.bigtwine.services.socials.domain.Account;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;

public final class DefaultAccountCreator<A> implements AccountCreator<A> {

    public DefaultAccountCreator() {
    }

    @Override
    public Account execute(Connection<A> connection) throws Exception {
        UserProfile profile = connection.fetchUserProfile();

        return new Account()
            .login(profile.getUsername())
            .email(profile.getEmail())
            .firstName(profile.getFirstName())
            .lastName(profile.getLastName())
            .imageUrl(connection.getImageUrl());
    }
}

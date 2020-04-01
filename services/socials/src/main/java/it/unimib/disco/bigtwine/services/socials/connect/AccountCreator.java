package it.unimib.disco.bigtwine.services.socials.connect;

import it.unimib.disco.bigtwine.services.socials.domain.Account;
import org.springframework.social.connect.Connection;

public interface AccountCreator<A> {
    Account execute(Connection<A> connection) throws Exception;
}

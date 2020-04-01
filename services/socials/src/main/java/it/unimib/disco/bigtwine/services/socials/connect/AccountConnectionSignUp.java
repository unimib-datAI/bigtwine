package it.unimib.disco.bigtwine.services.socials.connect;

import it.unimib.disco.bigtwine.services.socials.domain.Account;
import it.unimib.disco.bigtwine.services.socials.service.AccountService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountConnectionSignUp implements ConnectionSignUp {

    private final AccountService accountService;
    private final AccountCreatorLocator accountCreatorLocator;

    public AccountConnectionSignUp(AccountService accountService, AccountCreatorLocator accountCreatorLocator) {
        this.accountService = accountService;
        this.accountCreatorLocator = accountCreatorLocator;
    }

    private <A> Account createAccount(Connection<A> connection) throws Exception {
        return this.accountCreatorLocator
            .getAccountCreator(connection.getApi())
            .execute(connection);
    }

    @Override
    public String execute(Connection<?> connection) {
        try {
            Account account = this.createAccount(connection);
            Optional<Account> registeredAccount = this.accountService.registerAccount(account);

            return registeredAccount.map(Account::getId).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}

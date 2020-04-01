package it.unimib.disco.bigtwine.services.socials.service;

import it.unimib.disco.bigtwine.services.socials.client.AuthServiceClient;
import it.unimib.disco.bigtwine.services.socials.connect.dto.AccountDTO;
import it.unimib.disco.bigtwine.services.socials.domain.Account;
import it.unimib.disco.bigtwine.services.socials.security.AuthoritiesConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private AuthServiceClient authServiceClient;

    private AccountService accountService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        this.accountService = new AccountService(this.authServiceClient);

        when(authServiceClient.createAccount(any()))
            .thenAnswer((a) -> {
                AccountDTO reqAccount = a.getArgument(0);
                assertNotNull(reqAccount);
                Account account = this.createAccount()
                    .id("1")
                    .activated(true)
                    .login(reqAccount.getLogin())
                    .email(reqAccount.getEmail())
                    .firstName(reqAccount.getFirstName())
                    .lastName(reqAccount.getLastName())
                    .imageUrl(reqAccount.getImageUrl());
                account.setAuthorities(reqAccount.getAuthorities());

                return account;
            });

        when(authServiceClient.findAccountById(anyString()))
            .thenAnswer((a) -> {
                return this.createAccount()
                    .id(a.getArgument(0))
                    .activated(true)
                    .login("login")
                    .email("user@email.com")
                    .firstName("firstname")
                    .lastName("lastname")
                    .imageUrl("http://fakehost/image.png")
                    .authorities(AuthoritiesConstants.USER);
            });
    }

    @Test
    public void testRegisterAccount() {
        Account account = this.createAccount()
            .login("User Name")
            .email("email@email.com");
        Optional<Account> registeredAccount = this.accountService.registerAccount(account);

        assertTrue(registeredAccount.isPresent());
        assertEquals("UserName", registeredAccount.get().getLogin());

        verify(authServiceClient, times(1)).createAccount(any());
    }

    @Test
    public void testGetAccountById() {
        Optional<Account> account = this.accountService.getAccountById("1");

        assertTrue(account.isPresent());
        assertEquals("login", account.get().getLogin());

        verify(authServiceClient, times(1)).findAccountById(anyString());
    }

    private Account createAccount() {
        return new Account();
    }
}

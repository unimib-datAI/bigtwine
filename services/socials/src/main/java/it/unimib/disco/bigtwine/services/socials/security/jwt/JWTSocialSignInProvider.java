package it.unimib.disco.bigtwine.services.socials.security.jwt;

import it.unimib.disco.bigtwine.services.socials.connect.dto.UserSignInDTO;
import it.unimib.disco.bigtwine.services.socials.domain.Account;
import it.unimib.disco.bigtwine.services.socials.security.AuthDetailsConstants;
import it.unimib.disco.bigtwine.services.socials.service.AccountService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.Connection;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.*;

@Component
public class JWTSocialSignInProvider {

    private final AccountService accountService;
    private final TokenProvider tokenProvider;

    public JWTSocialSignInProvider(AccountService accountService, TokenProvider tokenProvider) {
        this.accountService = accountService;
        this.tokenProvider = tokenProvider;
    }

    private Authentication createAuthentication(String userId, UserDetails user, Connection<?> connection) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put(AuthDetailsConstants.USER_ID, userId);
        userDetails.put(AuthDetailsConstants.SOCIAL_SIGNIN_PROVIDER_ID, connection.getKey().getProviderId());
        userDetails.put(AuthDetailsConstants.SOCIAL_SIGNIN_USER_ID, connection.getKey().getProviderUserId());
        authentication.setDetails(userDetails);

        return authentication;
    }

    public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
        final boolean rememberMe = true;
        final UserDetails user = this.accountService
            .getAccountById(userId)
            .map(Account::createUserDetails)
            .orElse(null);

        if (user == null) {
            return null;
        }

        return tokenProvider.createToken(
            this.createAuthentication(userId, user, connection),
            rememberMe);
    }
}

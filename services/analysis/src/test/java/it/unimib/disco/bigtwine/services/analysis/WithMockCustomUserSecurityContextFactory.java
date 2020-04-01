package it.unimib.disco.bigtwine.services.analysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public final class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser withUser) {
        String userId = withUser.userId();
        Assert.hasLength(userId, "userId() must be non empty String");

        String username = withUser.username();
        if (username.isEmpty()) {
            username = userId;
        }

        UserDetails principal = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("user_id", userId);
        authentication.setDetails(userDetails);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}

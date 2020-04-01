package it.unimib.disco.bigtwine.services.analysis;

import it.unimib.disco.bigtwine.services.analysis.security.AuthoritiesConstants;
import it.unimib.disco.bigtwine.services.analysis.security.jwt.TokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        User basicUser1 = new User("testuser-1", "password", Collections.singletonList(
            new SimpleGrantedAuthority(AuthoritiesConstants.USER)
        ));
        User basicUser2 = new User("testuser-2", "password", Collections.singletonList(
            new SimpleGrantedAuthority(AuthoritiesConstants.USER)
        ));
        User adminUser = new User("adminuser-1", "password", Collections.singletonList(
            new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)
        ));

        return new InMemoryUserDetailsManager(basicUser1, basicUser2, adminUser);
    }
}

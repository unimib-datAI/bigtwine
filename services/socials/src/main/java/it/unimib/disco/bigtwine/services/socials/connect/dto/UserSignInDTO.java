package it.unimib.disco.bigtwine.services.socials.connect.dto;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

public class UserSignInDTO {
    private String id;
    private String login;
    private Set<String> authorities;

    public UserSignInDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserSignInDTO id(String id) {
        this.id = id;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public UserSignInDTO username(String username) {
        this.login = username;
        return this;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public UserSignInDTO authorities(Set<String> authorities) {
        this.authorities = authorities;
        return this;
    }
}

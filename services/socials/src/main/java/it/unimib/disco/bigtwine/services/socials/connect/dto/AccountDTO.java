package it.unimib.disco.bigtwine.services.socials.connect.dto;

import it.unimib.disco.bigtwine.services.socials.domain.Account;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AccountDTO {
    private String login;
    private String email;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private Set<String> authorities;
    private String password;
    private boolean activated;

    public AccountDTO() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public AccountDTO login(String login) {
        this.login = login;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountDTO email(String email) {
        this.email = email;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public AccountDTO firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public AccountDTO lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public AccountDTO imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountDTO password(String password) {
        this.password = password;
        return this;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public AccountDTO activated(boolean activated) {
        this.activated = activated;
        return this;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public AccountDTO authorities(Set<String> authorities) {
        this.setAuthorities(authorities);

        return this;
    }

    public AccountDTO authorities(String... authorities) {
        Set<String> a = new HashSet<>(Arrays.asList(authorities));
        this.setAuthorities(a);

        return this;
    }
}

package it.unimib.disco.bigtwine.services.socials.connect.dto;

public class UserSignedUpDTO {
    private String id;
    private String login;
    private boolean activated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserSignedUpDTO id(String id) {
        this.id = id;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public UserSignedUpDTO login(String login) {
        this.login = login;
        return this;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public UserSignedUpDTO activated(boolean activated) {
        this.activated = activated;
        return this;
    }
}

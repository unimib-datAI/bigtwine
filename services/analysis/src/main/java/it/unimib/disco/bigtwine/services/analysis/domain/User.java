package it.unimib.disco.bigtwine.services.analysis.domain;

import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Field("uid")
    private String uid;

    @Field("username")
    private String username;

    public User() {}

    public String getUid() {
        return uid;
    }

    public User uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public User username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        if (user.getUid() == null || getUid() == null) {
            return false;
        }
        return Objects.equals(getUid(), user.getUid());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUid());
    }

    @Override
    public String toString() {
        return "User{" +
            "uid=" + getUid() +
            ", username='" + getUsername() + "'" +
            "}";
    }
}

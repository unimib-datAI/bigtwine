package it.unimib.disco.bigtwine.services.jobsupervisor.domain;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String uid;
    private String username;

    public UserInfo() { }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

package it.unimib.disco.bigtwine.services.socials.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.social.oauth1.OAuthToken;

import java.time.Instant;

@Document(collection = "request_token")
public class RequestToken {
    @Id
    @Field("id")
    private String id;

    @Field("created_at")
    private Instant createdAt;

    @Field("token")
    private OAuthToken token;

    public String getId() {
        return id;
    }

    public RequestToken id(String id) {
        this.id = id;
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public RequestToken createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public OAuthToken getToken() {
        return token;
    }

    public RequestToken token(OAuthToken token) {
        this.token = token;
        return this;
    }

    public void setToken(OAuthToken token) {
        this.token = token;
    }
}

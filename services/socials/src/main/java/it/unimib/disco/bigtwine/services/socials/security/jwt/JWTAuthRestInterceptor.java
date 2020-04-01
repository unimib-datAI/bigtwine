package it.unimib.disco.bigtwine.services.socials.security.jwt;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class JWTAuthRestInterceptor implements ClientHttpRequestInterceptor {

    private String token;

    public JWTAuthRestInterceptor(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + this.getToken());
        return execution.execute(request, body);
    }
}

package it.unimib.disco.bigtwine.services.socials.client;

import it.unimib.disco.bigtwine.services.socials.security.SecurityUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import it.unimib.disco.bigtwine.services.socials.security.jwt.TokenProvider;
import org.springframework.stereotype.Component;

@Component
public class UserFeignClientInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";
    private final TokenProvider tokenProvider;

    public UserFeignClientInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void apply(RequestTemplate template) {
        String jwt = this.tokenProvider.createSystemToken();
        template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER, jwt));
    }
}

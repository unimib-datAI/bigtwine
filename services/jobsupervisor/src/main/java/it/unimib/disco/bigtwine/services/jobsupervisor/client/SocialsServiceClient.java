package it.unimib.disco.bigtwine.services.jobsupervisor.client;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.OAuthCredentials;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "socials")
public interface SocialsServiceClient {
    @RequestMapping("/api/oauth/connection/{provider}/{user}")
    OAuthCredentials findOAuthCredentials(@PathVariable("provider") String provider, @PathVariable("user") String userId);

    default OAuthCredentials findTwitterOAuthCredentials(String userId) {
        return this.findOAuthCredentials("twitter", userId);
    }
}

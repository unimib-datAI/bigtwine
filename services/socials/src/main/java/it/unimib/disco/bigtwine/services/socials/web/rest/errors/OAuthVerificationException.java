package it.unimib.disco.bigtwine.services.socials.web.rest.errors;


public class OAuthVerificationException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public OAuthVerificationException() {
        super(null, "OAuth verification failed", "oauth", "oauthverificationfailed");
    }
}

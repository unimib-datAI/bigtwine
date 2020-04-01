package it.unimib.disco.bigtwine.services.socials.web.rest.errors;

public class InvalidOAuthCallbackUrlException extends BadRequestAlertException {
    private static final long serialVersionUID = 1L;

    public InvalidOAuthCallbackUrlException() {
        super(null, "OAuth authorization failed. Invalid callback url", "oauth", "invalidcallbackurl");
    }
}

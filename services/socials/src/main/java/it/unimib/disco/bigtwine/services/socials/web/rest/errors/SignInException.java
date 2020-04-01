package it.unimib.disco.bigtwine.services.socials.web.rest.errors;

public class SignInException extends BadRequestAlertException {
    public SignInException() {
        super(null, "Sign in couldn't be completed", "signin", "signinexception");
    }
}

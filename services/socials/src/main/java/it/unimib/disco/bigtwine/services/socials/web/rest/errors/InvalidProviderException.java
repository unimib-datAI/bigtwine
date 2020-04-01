package it.unimib.disco.bigtwine.services.socials.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class InvalidProviderException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public InvalidProviderException() {
        super(null, "Invalid provider", Status.BAD_REQUEST);
    }
}

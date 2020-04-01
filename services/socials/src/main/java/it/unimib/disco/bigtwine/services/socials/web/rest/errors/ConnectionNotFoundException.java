package it.unimib.disco.bigtwine.services.socials.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ConnectionNotFoundException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public ConnectionNotFoundException() {
        super(null, "Valid connection between the user and the provider not found", Status.NOT_FOUND);
    }
}

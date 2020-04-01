package it.unimib.disco.bigtwine.services.socials.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class UnauthorizedException extends AbstractThrowableProblem {
    public UnauthorizedException() {
        super(null, null, Status.UNAUTHORIZED);
    }
}

package it.unimib.disco.bigtwine.services.analysis.web.api.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Not authorized");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}

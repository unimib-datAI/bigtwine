package it.unimib.disco.bigtwine.services.analysis.web.api.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("User not allowed to access this resource");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}

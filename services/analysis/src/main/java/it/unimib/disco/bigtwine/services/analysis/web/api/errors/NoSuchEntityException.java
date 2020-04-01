package it.unimib.disco.bigtwine.services.analysis.web.api.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoSuchEntityException extends RuntimeException {
    public NoSuchEntityException(Class entityType, String entityId) {
        super(String.format("Entity of type %s with id %s not found", entityType.toString(), entityId));
    }

    public NoSuchEntityException(String message) {
        super(message);
    }
}

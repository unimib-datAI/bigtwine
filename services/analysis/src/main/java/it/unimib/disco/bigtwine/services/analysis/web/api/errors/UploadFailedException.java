package it.unimib.disco.bigtwine.services.analysis.web.api.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UploadFailedException extends RuntimeException {
    public UploadFailedException(String message) {
        super(message);
    }
}

package it.unimib.disco.bigtwine.streamprocessor;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class ExtendedJsonSerializer<T> extends JsonSerializer<T> {
    public ExtendedJsonSerializer() {
        super();
        objectMapper.registerModule(new JavaTimeModule());
    }
}

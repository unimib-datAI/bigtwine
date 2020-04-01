package it.unimib.disco.bigtwine.services.analysis.domain.mapper;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisResultPayload;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.TimeZone;

public interface AnalysisResultPayloadMapper {
    AnalysisResultPayload map(Object object);

    default OffsetDateTime fromInstant(Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, TimeZone.getTimeZone("UTC").toZoneId());
    }

    default Instant fromOffsetDateTime(OffsetDateTime dateTime) {
        return dateTime == null ? null : Instant.from(dateTime);
    }
}

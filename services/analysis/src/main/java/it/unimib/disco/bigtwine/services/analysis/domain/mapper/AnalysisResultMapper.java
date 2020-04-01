package it.unimib.disco.bigtwine.services.analysis.domain.mapper;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisResult;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.AnalysisResultDTO;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.TimeZone;

public interface AnalysisResultMapper {
    AnalysisResultDTO map(AnalysisResult result);

    default OffsetDateTime fromInstant(Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, TimeZone.getTimeZone("UTC").toZoneId());
    }

    default Instant fromOffsetDateTime(OffsetDateTime dateTime) {
        return dateTime == null ? null : Instant.from(dateTime);
    }
}

package it.unimib.disco.bigtwine.services.analysis.domain.mapper;

import it.unimib.disco.bigtwine.commons.messaging.dto.CoordinatesDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.LinkedEntityDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.NeelProcessedTweetDTO;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisResultPayload;
import it.unimib.disco.bigtwine.services.analysis.domain.LinkedEntity;
import it.unimib.disco.bigtwine.services.analysis.domain.NeelProcessedTweet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.TimeZone;

@Mapper
public interface NeelProcessedTweetMapper extends AnalysisResultPayloadMapper {
    NeelProcessedTweetMapper INSTANCE = Mappers.getMapper( NeelProcessedTweetMapper.class );

    NeelProcessedTweet neelProcessedTweetFromDto(NeelProcessedTweetDTO tweet);

    default GeoJsonPoint geoJsonPointFromCoordinate(CoordinatesDTO coordinates) {
        if (coordinates == null) {
            return null;
        }

        return new GeoJsonPoint(coordinates.getLongitude(), coordinates.getLatitude());
    }

    default URL urlFromString(String urlString) throws MalformedURLException {
        if (urlString == null) {
            return null;
        }
        return new URL(urlString);
    }

    @Mapping(source = "nil", target = "isNil")
    LinkedEntity linkedEntityDTOToLinkedEntity(LinkedEntityDTO linkedEntityDTO);

    @Override
    default AnalysisResultPayload map(Object object) {
        if (object instanceof NeelProcessedTweetDTO) {
            return neelProcessedTweetFromDto((NeelProcessedTweetDTO) object);
        } else {
            throw new IllegalArgumentException("object must be an instance of NeelProcessedTweetDTO");
        }
    }
}

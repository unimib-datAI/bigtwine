package it.unimib.disco.bigtwine.services.analysis.domain.mapper;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisResult;
import it.unimib.disco.bigtwine.services.analysis.domain.NeelProcessedTweet;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.AnalysisResultDTO;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.CoordinatesDTO;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.TwitterNeelAnalysisResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.net.URL;

@Mapper
public interface TwitterNeelAnalysisResultMapper extends AnalysisResultMapper {
    TwitterNeelAnalysisResultMapper INSTANCE = Mappers.getMapper( TwitterNeelAnalysisResultMapper.class );

    @Mapping(source = "analysis.id", target = "analysisId")
    TwitterNeelAnalysisResultDTO analysisResultDtoFromModel(AnalysisResult<NeelProcessedTweet> result);

    default CoordinatesDTO coordinatesFromGeoJsonPoint(GeoJsonPoint point) {
        if (point == null) {
            return null;
        }

        return new CoordinatesDTO()
            .longitude(point.getX())
            .latitude(point.getY());
    }

    default String stringUrlFromUrl(URL url) {
        if (url == null) {
            return null;
        }

        return url.toString();
    }

    @Override
    default AnalysisResultDTO map(AnalysisResult result) {
        if (result.getPayload() instanceof NeelProcessedTweet) {
            @SuppressWarnings("unchecked")
            AnalysisResult<NeelProcessedTweet> _result = (AnalysisResult<NeelProcessedTweet>) result;
            return analysisResultDtoFromModel(_result);
        } else {
            throw new IllegalArgumentException("object payload must be an instance of NeelProcessedTweet");
        }
    }
}

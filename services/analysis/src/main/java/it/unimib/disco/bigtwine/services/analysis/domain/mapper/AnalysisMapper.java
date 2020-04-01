package it.unimib.disco.bigtwine.services.analysis.domain.mapper;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimib.disco.bigtwine.services.analysis.domain.*;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisErrorCode;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisVisibility;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Mapper
public interface AnalysisMapper {

    AnalysisMapper INSTANCE = Mappers.getMapper( AnalysisMapper.class );
    ObjectMapper jsonMapper = new ObjectMapper();

    default ObjectMapper getObjectMapper() {
        jsonMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        return jsonMapper;
    }

    default OffsetDateTime fromInstant(Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, TimeZone.getTimeZone("UTC").toZoneId());
    }

    default Instant fromOffsetDateTime(OffsetDateTime dateTime) {
        return dateTime == null ? null : Instant.from(dateTime);
    }

    default GeoJsonPoint geoJsonPointFromCoordinatesDTO(CoordinatesDTO coords) {
        return new GeoJsonPoint(coords.getLongitude(), coords.getLatitude());
    }

    default CoordinatesDTO coordinatesFromGeoJsonPoint(GeoJsonPoint point) {
        return new CoordinatesDTO()
                .longitude(point.getX())
                .latitude(point.getY());
    }

    @Mapping(target = "tings", ignore = true)
    Analysis analysisFromAnalysisDTO(AnalysisDTO analysisDTO);

    List<Analysis> analysesFromAnalysisDTOs(List<AnalysisDTO> analysisDTOs);

    @Mapping(target = "tings", ignore = true)
    AnalysisDTO analysisDtoFromAnalysis(Analysis analysis);

    User userDTOToUser(UserDTO user);

    default User userFromUserMap(Map<Object, Object> userMap) {
        if (userMap == null) {
            return null;
        }

        return this.getObjectMapper().convertValue(userMap, User.class);
    }

    List<AnalysisDTO> analysisDtosFromAnalyses(List<Analysis> analyses);

    AnalysisVisibility visibilityFromVisibilityEnum(AnalysisVisibilityEnum visibility);

    AnalysisType analysisTypeFromTypeEnum(AnalysisTypeEnum analysisTypeEnum);

    default AnalysisInput analysisInputFromObject(Object input) {
        if (input instanceof AnalysisInputDTO) {
            return analysisInputFromAnalysisInputDTO((AnalysisInputDTO)input);
        } else if (input instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> mapInput = (Map<Object, Object>)input;
            return analysisInputFromMap(mapInput);
        } else {
            return null;
        }
    }

    default AnalysisInput analysisInputFromAnalysisInputDTO(AnalysisInputDTO input) {
        @SuppressWarnings("unchecked")
        Map<Object, Object> mapInput = (Map<Object, Object>)this.getObjectMapper().convertValue(input, Map.class);
        return this.analysisInputFromMap(mapInput);
    }

    default AnalysisInput analysisInputFromMap(Map<Object, Object> input) {
        if (input.containsKey("type") && input.get("type") instanceof String) {
            String type = (String)input.get("type");
            input.replace("type", type.toUpperCase().replace("-", "_"));
        }

        return this.getObjectMapper().convertValue(input, AnalysisInput.class);
    }

    default AnalysisInputDTO analysisInputDtoFromAnalysisInput(AnalysisInput input) {
        if (input instanceof QueryAnalysisInput) {
            return this.queryAnalysisInputDtoFromQueryAnalysisInput((QueryAnalysisInput)input);
        } else if (input instanceof DatasetAnalysisInput) {
            return this.datasetAnalysisInputDtoFromDatasetAnalysisInput((DatasetAnalysisInput)input);
        } else if (input instanceof GeoAreaAnalysisInput) {
            return this.geoAreaAnalysisInputDtoFromGeoAreaAnalysisInput((GeoAreaAnalysisInput)input);
        } else {
            throw new UnsupportedOperationException("Unsupported input type " + input.getClass());
        }
    }

    QueryAnalysisInputDTO queryAnalysisInputDtoFromQueryAnalysisInput(QueryAnalysisInput input);

    DatasetAnalysisInputDTO datasetAnalysisInputDtoFromDatasetAnalysisInput(DatasetAnalysisInput input);

    GeoAreaAnalysisInputDTO geoAreaAnalysisInputDtoFromGeoAreaAnalysisInput(GeoAreaAnalysisInput input);

    QueryAnalysisInput queryAnalysisInputFromQueryAnalysisInputDTO(QueryAnalysisInputDTO input);

    DatasetAnalysisInput datasetAnalysisInputFromDatasetAnalysisInputDTO(DatasetAnalysisInputDTO input);

    GeoAreaAnalysisInput geoAreaAnalysisInputFromGeoAreaAnalysisInputDTO(GeoAreaAnalysisInputDTO input);

    AnalysisStatus statusFromStatusEnum(AnalysisStatusEnum status);

    AnalysisStatusHistoryDTO statusHistoryDTOFromStatusHistory(AnalysisStatusHistory statusHistory);

    List<AnalysisStatusHistoryDTO> statusHistoryDTOsFromStatusHistories(List<AnalysisStatusHistory> statusHistories);

    default AnalysisErrorCode analysisErrorCodeFromInt(Integer value) {
        if (value != null) {
            return AnalysisErrorCode.valueOf(value);
        } else {
            return null;
        }
    }

    default Integer intFromAnalysisErrorCode(AnalysisErrorCode value) {
        if (value != null) {
            return value.getValue();
        } else {
            return 0;
        }
    }

    AnalysisExportDTO analysisExportDTOFromAnalysisExport(AnalysisExport export);
    AnalysisExport analysisExportFromAnalysisExportDTO(AnalysisExportDTO export);
}

package it.unimib.disco.bigtwine.services.analysis.domain.mapper;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSettingResolved;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.AnalysisSettingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AnalysisSettingMapper {
    AnalysisSettingMapper INSTANCE = Mappers.getMapper( AnalysisSettingMapper.class );

    List<AnalysisSettingDTO> dtosFromAnalysisSettingResolved(List<AnalysisSettingResolved> settings);
}

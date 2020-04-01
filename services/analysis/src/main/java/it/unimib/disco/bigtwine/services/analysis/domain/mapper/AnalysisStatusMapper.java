package it.unimib.disco.bigtwine.services.analysis.domain.mapper;

import it.unimib.disco.bigtwine.commons.models.AnalysisStatusEnum;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AnalysisStatusMapper {
    AnalysisStatusMapper INSTANCE = Mappers.getMapper( AnalysisStatusMapper.class );

    AnalysisStatusEnum analysisStatusEventEnumFromDomain(AnalysisStatus status);
    AnalysisStatus analysisStatusFromEventEnum(AnalysisStatusEnum status);
}

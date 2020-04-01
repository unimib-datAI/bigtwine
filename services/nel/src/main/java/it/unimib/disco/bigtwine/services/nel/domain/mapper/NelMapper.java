package it.unimib.disco.bigtwine.services.nel.domain.mapper;

import it.unimib.disco.bigtwine.commons.messaging.dto.LinkedEntityDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.LinkedTextDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.RecognizedTextDTO;
import it.unimib.disco.bigtwine.services.nel.domain.LinkedEntity;
import it.unimib.disco.bigtwine.services.nel.domain.LinkedText;
import it.unimib.disco.bigtwine.services.nel.domain.RecognizedText;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NelMapper {
    NelMapper INSTANCE = Mappers.getMapper( NelMapper.class );

    RecognizedText[] recognizedTextsFromDTOs(RecognizedTextDTO[] recognizedTextDTOS);
    LinkedTextDTO[] dtosFromLinkedTexts(LinkedText[] linkedTexts);
    @Mapping(target = "resource", ignore = true)
    LinkedEntityDTO dtoFromLinkedEntity(LinkedEntity linkedEntity);
}

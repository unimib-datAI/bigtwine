package it.unimib.disco.bigtwine.services.ner.domain.mapper;

import it.unimib.disco.bigtwine.commons.messaging.dto.PlainTextDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.RecognizedTextDTO;
import it.unimib.disco.bigtwine.services.ner.domain.PlainText;
import it.unimib.disco.bigtwine.services.ner.domain.RecognizedText;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NerMapper {
    NerMapper INSTANCE = Mappers.getMapper( NerMapper.class );

    PlainText plainTextFromDTO(PlainTextDTO plainTextDTO);
    PlainText[] plainTextsFromDTOs(PlainTextDTO[] plainTextDTO);
    RecognizedTextDTO dtoFromRecognizedText(RecognizedText recognizedText);
    RecognizedTextDTO[] dtosFromRecognizedTexts(RecognizedText[] plainText);
}

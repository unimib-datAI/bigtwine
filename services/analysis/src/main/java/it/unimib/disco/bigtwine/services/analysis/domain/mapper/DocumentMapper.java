package it.unimib.disco.bigtwine.services.analysis.domain.mapper;

import it.unimib.disco.bigtwine.services.analysis.domain.Document;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.DocumentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.TimeZone;

@Mapper
public interface DocumentMapper {
    DocumentMapper INSTANCE = Mappers.getMapper( DocumentMapper.class );

    default OffsetDateTime fromInstant(Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, TimeZone.getTimeZone("UTC").toZoneId());
    }

    default Instant fromOffsetDateTime(OffsetDateTime dateTime) {
        return dateTime == null ? null : Instant.from(dateTime);
    }

    DocumentDTO dtoFromDocument(Document document);
    Document documentFromDTO(DocumentDTO documentDTO);

    List<DocumentDTO> dtosFromDocuments(List<Document> documents);
    List<Document> documentsFromDTOs(List<DocumentDTO> documentDTOs);
}

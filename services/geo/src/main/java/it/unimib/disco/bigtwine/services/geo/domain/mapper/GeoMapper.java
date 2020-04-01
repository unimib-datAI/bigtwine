package it.unimib.disco.bigtwine.services.geo.domain.mapper;

import it.unimib.disco.bigtwine.commons.messaging.dto.DecodedLocationDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.LocationDTO;
import it.unimib.disco.bigtwine.services.geo.domain.DecodedLocation;
import it.unimib.disco.bigtwine.services.geo.domain.Location;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GeoMapper {
    GeoMapper INSTANCE = Mappers.getMapper( GeoMapper.class );

    Location locationFromDTO(LocationDTO locationDTO);
    Location[] locationsFromDTOs(LocationDTO[] locationDTOS);
    DecodedLocationDTO dtoFromDecodedLocation(DecodedLocation decodedLocation);
    DecodedLocationDTO[] dtosFromDecodedLocations(DecodedLocation[] decodedLocation);
}

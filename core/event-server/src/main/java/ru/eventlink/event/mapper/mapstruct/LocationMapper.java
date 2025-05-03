package ru.eventlink.event.mapper.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.eventlink.dto.event.LocationDto;
import ru.eventlink.event.model.Location;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    Location locationDtoToLocation(LocationDto locationDto);

    LocationDto locationToLocationDto(Location location);
}

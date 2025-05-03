package ru.eventlink.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.eventlink.dto.event.*;
import ru.eventlink.event.mapper.mapstruct.EventMapper;
import ru.eventlink.event.mapper.mapstruct.LocationMapper;
import ru.eventlink.event.mapper.mapstruct.RecommendationsMapper;
import ru.eventlink.event.model.Event;
import ru.eventlink.event.model.Location;
import ru.eventlink.stats.proto.RecommendedEventProto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MapperMapstruct implements Mapper {
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final RecommendationsMapper recommendationsMapper;

    @Override
    public Event newEventDtoToEvent(NewEventDto newEventDto) {
        return eventMapper.newEventDtoToEvent(newEventDto);
    }

    @Override
    public EventFullDto eventToEventFullDto(Event event) {
        return eventMapper.eventToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> listEventToListEventShortDto(List<Event> events) {
        return eventMapper.listEventToListEventShortDto(events);
    }

    @Override
    public List<EventFullDto> listEventToListEventFullDto(List<Event> events) {
        return eventMapper.listEventToListEventFullDto(events);
    }

    @Override
    public Location locationDtoToLocation(LocationDto locationDto) {
        return locationMapper.locationDtoToLocation(locationDto);
    }

    @Override
    public List<RecommendationsDto> listRecommendedEventProtoToListRecommendationsDto(List<RecommendedEventProto> recommendations) {
        return recommendationsMapper.listRecommendedEventProtoToListRecommendationsDto(recommendations);
    }
}

package ru.eventlink.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.eventlink.dto.event.*;
import ru.eventlink.event.model.Event;
import ru.eventlink.event.model.Location;
import ru.eventlink.stats.proto.RecommendedEventProto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Mapper {
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final RecommendationsMapper recommendationsMapper;

    public Event newEventDtoToEvent(NewEventDto newEventDto) {
        return eventMapper.newEventDtoToEvent(newEventDto);
    }

    public EventFullDto eventToEventFullDto(Event event) {
        return eventMapper.eventToEventFullDto(event);
    }

    public List<EventShortDto> listEventToListEventShortDto(List<Event> events) {
        return eventMapper.listEventToListEventShortDto(events);
    }

    public List<EventFullDto> listEventToListEventFullDto(List<Event> events) {
        return eventMapper.listEventToListEventFullDto(events);
    }

    public Location locationDtoToLocation(LocationDto locationDto) {
        return locationMapper.locationDtoToLocation(locationDto);
    }

    public List<RecommendationsDto> listRecommendedEventProtoToListRecommendationsDto(List<RecommendedEventProto> recommendations) {
        return recommendationsMapper.listRecommendedEventProtoToListRecommendationsDto(recommendations);
    }
}

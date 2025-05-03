package ru.eventlink.event.mapper;

import ru.eventlink.dto.event.*;
import ru.eventlink.event.model.Event;
import ru.eventlink.event.model.Location;
import ru.eventlink.stats.proto.RecommendedEventProto;

import java.util.List;

public interface Mapper {
    Event newEventDtoToEvent(NewEventDto newEventDto);

    EventFullDto eventToEventFullDto(Event event);

    List<EventShortDto> listEventToListEventShortDto(List<Event> events);

    List<EventFullDto> listEventToListEventFullDto(List<Event> events);

    Location locationDtoToLocation(LocationDto locationDto);

    List<RecommendationsDto> listRecommendedEventProtoToListRecommendationsDto(List<RecommendedEventProto> recommendations);
}

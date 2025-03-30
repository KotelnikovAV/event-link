package ru.eventlink.event.service;

import ru.eventlink.dto.event.*;
import ru.eventlink.dto.requests.EventRequestStatusUpdateRequestDto;
import ru.eventlink.dto.requests.EventRequestStatusUpdateResultDto;
import ru.eventlink.dto.requests.ParticipationRequestDto;

import java.util.List;

public interface EventPrivateService {
    EventFullDto addEvent(NewEventDto newEventDto, long userId);

    EventFullDto findEventByUserIdAndEventId(long userId, long eventId);

    List<EventShortDto> findEventsByUser(long userId, int page, int size);

    EventFullDto updateEvent(UpdateEventUserRequest updateEventUserRequest, long userId, long eventId);

    List<ParticipationRequestDto> findRequestByEventId(long userId, long eventId);

    EventRequestStatusUpdateResultDto updateRequestByEventId(EventRequestStatusUpdateRequestDto updateRequest,
                                                             long userId,
                                                             long eventId);

    List<RecommendationsDto> findRecommendations(long userId);
}

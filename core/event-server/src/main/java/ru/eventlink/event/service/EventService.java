package ru.eventlink.event.service;

import ru.eventlink.dto.event.*;
import ru.eventlink.dto.requests.EventRequestStatusUpdateRequestDto;
import ru.eventlink.dto.requests.EventRequestStatusUpdateResultDto;
import ru.eventlink.dto.requests.ParticipationRequestDto;
import ru.eventlink.enums.EventPublicSort;
import ru.eventlink.enums.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(NewEventDto newEventDto, long userId);

    EventFullDto findEventByUserIdAndEventId(long userId, long eventId);

    List<EventShortDto> findEventsByUser(long userId, int page, int size);

    EventFullDto updateEvent(UpdateEventUserRequest updateEventUserRequest, long userId, long eventId);

    List<ParticipationRequestDto> findRequestByEventId(long userId, long eventId);

    EventRequestStatusUpdateResultDto updateRequestByEventId(EventRequestStatusUpdateRequestDto updateRequest,
                                                             long userId,
                                                             long eventId);

    List<EventShortDto> findAllPublicEvents(String text, List<Long> categories, Boolean paid,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            boolean onlyAvailable, EventPublicSort sort, int page, int size);

    EventFullDto findPublicEventById(long id);

    List<EventFullDto> findAllAdminEvents(List<Long> users, State state, List<Long> categories, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, int page, int size, boolean sortRating);

    EventFullDto updateEventAdmin(UpdateEventAdminRequest updateEventAdminRequest, long eventId);

    boolean findExistEventByEventIdAndInitiatorId(Long eventId, Long initiatorId);

    EventFullDto findEventById(Long eventId);

    boolean findExistEventByEventId(Long eventId);

    void updateRatingEvent(Long eventId, int rating);

    List<RecommendationsDto> findRecommendations(long userId);
}

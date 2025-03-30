package ru.eventlink.event.service;

import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.dto.event.UpdateEventAdminRequest;
import ru.eventlink.enums.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminService {
    List<EventFullDto> findAllAdminEvents(List<Long> users, State state, List<Long> categories, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, int page, int size, boolean sortRating);

    EventFullDto updateEventAdmin(UpdateEventAdminRequest updateEventAdminRequest, long eventId);

    boolean findExistEventByEventIdAndInitiatorId(Long eventId, Long initiatorId);

    EventFullDto findEventById(Long eventId);

    boolean findExistEventByEventId(Long eventId);

    void updateRatingEvent(Long eventId, int rating);
}

package ru.eventlink.fallback.event;

import org.springframework.stereotype.Component;
import ru.eventlink.client.event.EventAdminClient;
import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.dto.event.UpdateEventAdminRequest;
import ru.eventlink.enums.State;
import ru.eventlink.exception.ServerUnavailableException;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventAdminFallback implements EventAdminClient {
    @Override
    public List<EventFullDto> findAllAdminEvents(List<Long> users,
                                                 State state,
                                                 List<Long> categories,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 int page,
                                                 int size,
                                                 Boolean sortRating) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/events method GET is unavailable");
    }

    @Override
    public EventFullDto updateEventAdmin(UpdateEventAdminRequest updateEventAdminRequest, long eventId) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/events/{eventId} method PATCH is unavailable");
    }

    @Override
    public boolean findExistEventByEventIdAndInitiatorId(Long eventId, Long initiatorId) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/events/{eventId}/existence/{initiatorId} method GET is unavailable");
    }

    @Override
    public boolean findExistEventByEventId(Long eventId) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/events/{eventId}/existence/ method GET is unavailable");
    }

    @Override
    public EventFullDto findEventById(Long eventId) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/events/{eventId} method GET is unavailable");
    }

    @Override
    public void updateRatingEvent(long eventId, int rating) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/events/{eventId} method PATCH is unavailable");
    }
}

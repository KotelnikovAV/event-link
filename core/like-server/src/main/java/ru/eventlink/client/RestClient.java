package ru.eventlink.client;

import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.enums.Status;

public interface RestClient {
    boolean getUserExists(long userId);

    EventFullDto findEventById(long eventId);

    boolean findExistRequests(long eventId, long userId, Status status);

    void updateRatingUser(long userId, int difference);

    void updateRatingEvent(long eventId, int difference);
}

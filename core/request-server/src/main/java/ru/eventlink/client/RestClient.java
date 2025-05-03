package ru.eventlink.client;

import ru.eventlink.dto.event.EventFullDto;

public interface RestClient {
    boolean getUserExists(long userId);

    EventFullDto findEventById(long eventId);

    boolean findExistEventByEventIdAndInitiatorId(long eventId, long userId);
}

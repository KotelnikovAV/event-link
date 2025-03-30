package ru.eventlink.event.service;

import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.dto.event.EventShortDto;
import ru.eventlink.enums.EventPublicSort;

import java.time.LocalDateTime;
import java.util.List;

public interface EventPublicService {
    List<EventShortDto> findAllPublicEvents(String text, List<Long> categories, Boolean paid,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            boolean onlyAvailable, EventPublicSort sort, int page, int size);

    EventFullDto findPublicEventById(long id);
}

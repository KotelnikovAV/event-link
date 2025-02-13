package ru.eventlink.service;

import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.enums.StatusLike;

public interface LikeService {
    EventFullDto addLike(long eventId, long userId, StatusLike statusLike);

    EventFullDto updateLike(long eventId, long userId, StatusLike statusLike);

    void deleteLike(long eventId, long userId);
}

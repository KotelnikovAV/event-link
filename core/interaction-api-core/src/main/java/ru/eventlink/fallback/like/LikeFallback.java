package ru.eventlink.fallback.like;

import org.springframework.stereotype.Component;
import ru.eventlink.client.like.LikeClient;
import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.enums.StatusLike;
import ru.eventlink.exception.ServerUnavailableException;

@Component
public class LikeFallback implements LikeClient {
    @Override
    public EventFullDto addLike(long eventId, long userId, StatusLike reaction) {
        throw new ServerUnavailableException("Endpoint /api/v1/event/{eventId}/like/{userId} method POST is unavailable");
    }

    @Override
    public EventFullDto updateLike(long eventId, long userId, StatusLike reaction) {
        throw new ServerUnavailableException("Endpoint /api/v1/event/{eventId}/like/{userId} method PATCH is unavailable");
    }

    @Override
    public void deleteLike(long eventId, long userId) {
        throw new ServerUnavailableException("Endpoint /api/v1/event/{eventId}/like/{userId} method DELETE is unavailable");
    }
}

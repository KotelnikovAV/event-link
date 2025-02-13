package ru.eventlink.client.like;

import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.enums.StatusLike;
import ru.eventlink.fallback.like.LikeFallback;

@FeignClient(name = "like-server", fallback = LikeFallback.class)
public interface LikeClient {
    @PostMapping("/api/v1/event/{eventId}/like/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto addLike(@PathVariable @Positive long eventId,
                         @PathVariable @Positive long userId,
                         @RequestParam StatusLike reaction);

    @PatchMapping("/api/v1/event/{eventId}/like/{userId}")
    EventFullDto updateLike(@PathVariable @Positive long eventId,
                            @PathVariable @Positive long userId,
                            @RequestParam StatusLike reaction);

    @DeleteMapping("/api/v1/event/{eventId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteLike(@PathVariable @Positive long eventId,
                    @PathVariable @Positive long userId);
}

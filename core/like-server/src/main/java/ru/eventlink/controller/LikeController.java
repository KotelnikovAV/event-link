package ru.eventlink.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.enums.StatusLike;
import ru.eventlink.service.LikeService;

@RestController
@RequestMapping("/api/v1/event/{eventId}/like/{userId}")
@RequiredArgsConstructor
@Slf4j
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addLike(@PathVariable @Positive long eventId,
                                @PathVariable @Positive long userId,
                                @RequestParam StatusLike reaction) {
        log.info("Add like to an event with eventId = {}, by user with user_id = {}, reaction = {}",
                eventId, userId, reaction);
        return likeService.addLike(eventId, userId, reaction);
    }

    @PatchMapping
    public EventFullDto updateLike(@PathVariable @Positive long eventId,
                                   @PathVariable @Positive long userId,
                                   @RequestParam StatusLike reaction) {
        log.info("Patch like to an event with eventId = {}, by user with user_id = {}, reaction = {}",
                eventId, userId, reaction);
        return likeService.updateLike(eventId, userId, reaction);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable @Positive long eventId,
                           @PathVariable @Positive long userId) {
        log.info("Delete like to an event with eventId = {}, by user with user_id = {}",
                eventId, userId);
        likeService.deleteLike(eventId, userId);
    }
}

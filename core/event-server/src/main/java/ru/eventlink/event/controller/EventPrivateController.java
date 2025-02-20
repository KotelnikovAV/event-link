package ru.eventlink.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.dto.event.*;
import ru.eventlink.dto.requests.EventRequestStatusUpdateRequestDto;
import ru.eventlink.dto.requests.EventRequestStatusUpdateResultDto;
import ru.eventlink.dto.requests.ParticipationRequestDto;
import ru.eventlink.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@RequestBody @Valid NewEventDto newEventDto, @PathVariable long userId) {
        log.info("Received a POST request to add event {} from a user with an userId = {}", newEventDto, userId);
        return eventService.addEvent(newEventDto, userId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventById(@PathVariable long userId,
                                      @PathVariable long eventId) {
        log.info("Received a GET request to find event by id {} from a user with an userId = {}", eventId, userId);
        return eventService.findEventByUserIdAndEventId(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> findEventsByUser(@PathVariable long userId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Received a GET request to find events by userId = {} page = {} size = {}", userId, page, size);
        return eventService.findEventsByUser(userId, page, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@RequestBody @Valid UpdateEventUserRequest updateEventUserRequest,
                                    @PathVariable long userId,
                                    @PathVariable long eventId) {
        log.info("Received a PATCH request to update event with an eventId = {} from a user with an userId = {}, " +
                "request body {}", eventId, userId, updateEventUserRequest);
        return eventService.updateEvent(updateEventUserRequest, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findRequestByEventId(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Received a GET request to find request by event id = {} from a user with an userId = {}",
                eventId, userId);
        return eventService.findRequestByEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto updateRequestByEventId(@RequestBody
                                                                    @Valid
                                                                    EventRequestStatusUpdateRequestDto updateRequests,
                                                                    @PathVariable long userId,
                                                                    @PathVariable long eventId) {
        log.info("Received a PATCH request to update request with an eventId = {} from a user with an userId = {}, " +
                "request body {}", eventId, userId, updateRequests);
        return eventService.updateRequestByEventId(updateRequests, userId, eventId);
    }

    @GetMapping("/recommendations")
    public List<RecommendationsDto> findRecommendations(@PathVariable long userId) {
        log.info("Received a GET request to find recommendations");
        return eventService.findRecommendations(userId);
    }
}

package ru.eventlink.event.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.dto.event.EventShortDto;
import ru.eventlink.enums.EventPublicSort;
import ru.eventlink.event.service.EventPublicService;
import ru.eventlink.utility.Constants;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class EventPublicController {
    private final EventPublicService eventPublicService;

    @GetMapping
    public List<EventShortDto> findAllPublicEvents(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
                                                  LocalDateTime rangeStart,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
                                                  LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam(defaultValue = "EVENT_DATE") EventPublicSort sort,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get all public events by text {}", text);
        return eventPublicService.findAllPublicEvents(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, page, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findPublicEventById(@PathVariable Long eventId) {
        log.info("Get public event by id {}", eventId);
        return eventPublicService.findPublicEventById(eventId);
    }
}

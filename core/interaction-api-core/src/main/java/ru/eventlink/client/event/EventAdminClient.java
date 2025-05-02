package ru.eventlink.client.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.dto.event.UpdateEventAdminRequest;
import ru.eventlink.enums.State;
import ru.eventlink.fallback.event.EventAdminFallback;
import ru.eventlink.utility.Constants;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "event-server", fallback = EventAdminFallback.class)
public interface EventAdminClient {
    @GetMapping("/api/v1/admin/events")
    List<EventFullDto> findAllAdminEvents(@RequestParam(required = false) List<Long> users,
                                          @RequestParam(required = false) State state,
                                          @RequestParam(required = false) List<Long> categories,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
                                          LocalDateTime rangeStart,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
                                          LocalDateTime rangeEnd,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                          @RequestParam(defaultValue = "10") @Positive int size,
                                          @RequestParam(defaultValue = "true") Boolean sortRating);

    @PatchMapping("/api/v1/admin/events/{eventId}")
    EventFullDto updateEventAdmin(@RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest,
                                  @PathVariable long eventId);

    @GetMapping("/api/v1/admin/events/{eventId}/existence/{initiatorId}")
    boolean findExistEventByEventIdAndInitiatorId(@PathVariable @Positive Long eventId,
                                                  @PathVariable @Positive Long initiatorId);

    @GetMapping("/api/v1/admin/events/{eventId}/existence")
    boolean findExistEventByEventId(@PathVariable @Positive Long eventId);

    @GetMapping("/api/v1/admin/events/{eventId}")
    EventFullDto findEventById(@PathVariable Long eventId);

    @PutMapping("/api/v1/admin/events/{eventId}")
    void updateRatingEvent(@PathVariable long eventId,
                           @RequestParam int rating);
}

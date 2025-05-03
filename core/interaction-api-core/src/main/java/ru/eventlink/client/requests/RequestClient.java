package ru.eventlink.client.requests;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.dto.requests.ParticipationRequestDto;
import ru.eventlink.enums.Status;
import ru.eventlink.fallback.requests.RequestFallback;

import java.util.List;
import java.util.Set;

@FeignClient(name = "request-server", fallback = RequestFallback.class)
public interface RequestClient {
    @GetMapping("/api/v1/users/{userId}/requests")
    List<ParticipationRequestDto> findAllRequestsByUserId(@PathVariable Long userId);

    @PostMapping("/api/v1/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    ParticipationRequestDto addRequest(@PathVariable Long userId,
                                       @RequestParam Long eventId);

    @PatchMapping("/api/v1/users/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                          @PathVariable Long requestId);

    @GetMapping("/api/v1/users/requests/events/{eventId}")
    List<ParticipationRequestDto> findAllRequestsByEventId(@PathVariable Long eventId,
                                                           @RequestParam Status status);

    @GetMapping("/api/v1/users/requests")
    List<ParticipationRequestDto> findAllRequestsByRequestsId(@RequestParam Set<Long> requestsId);

    @PutMapping("/api/v1/users/requests/status")
    List<ParticipationRequestDto> updateRequest(@RequestParam Set<Long> requestsId,
                                                @RequestParam Status status);

    @GetMapping("api/v1/users/requests/existence")
    boolean findExistRequests(@RequestParam Long eventId,
                              @RequestParam Long userId,
                              @RequestParam Status status);
}

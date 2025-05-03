package ru.eventlink.client;

import ru.eventlink.dto.requests.ParticipationRequestDto;
import ru.eventlink.enums.Status;

import java.util.List;
import java.util.Set;

public interface RestClient {
    boolean getUserExists(long userId);

    List<ParticipationRequestDto> findAllRequestsByEventId(long eventId, Status status);

    List<ParticipationRequestDto> findAllRequestsByRequestsId(Set<Long> requestsId);

    List<ParticipationRequestDto> updateRequest(Set<Long> requestsId, Status status);
}

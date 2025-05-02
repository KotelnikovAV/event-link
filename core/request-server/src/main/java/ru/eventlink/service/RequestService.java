package ru.eventlink.service;

import ru.eventlink.dto.requests.ParticipationRequestDto;
import ru.eventlink.enums.Status;

import java.util.List;
import java.util.Set;

public interface RequestService {

    List<ParticipationRequestDto> findAllRequestsByUserId(long userId);

    ParticipationRequestDto addRequest(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);

    List<ParticipationRequestDto> findAllRequestsByRequestsId(Set<Long> requestsId);

    List<ParticipationRequestDto> updateRequest(Set<Long> requestsId, Status status);

    boolean findExistRequests(Long eventId, Long userId, String status);

    List<ParticipationRequestDto> findAllRequestsByEventIdAndStatus(Long eventId, Status status);
}

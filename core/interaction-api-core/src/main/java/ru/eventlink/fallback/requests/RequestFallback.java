package ru.eventlink.fallback.requests;

import org.springframework.stereotype.Component;
import ru.eventlink.client.requests.RequestClient;
import ru.eventlink.dto.requests.ParticipationRequestDto;
import ru.eventlink.enums.Status;
import ru.eventlink.exception.ServerUnavailableException;

import java.util.List;
import java.util.Set;

@Component
public class RequestFallback implements RequestClient {
    @Override
    public List<ParticipationRequestDto> findAllRequestsByUserId(Long userId) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/{userId}/requests method GET is unavailable");
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/{userId}/requests method POST is unavailable");
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/{userId}/requests/{requestId}/cancel method PATCH " +
                "is unavailable");
    }

    @Override
    public List<ParticipationRequestDto> findAllRequestsByEventId(Long eventId, Status status) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/requests/events/{eventId}/status method GET is unavailable");
    }

    @Override
    public List<ParticipationRequestDto> findAllRequestsByRequestsId(Set<Long> requestsId) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/requests method GET is unavailable");
    }

    @Override
    public List<ParticipationRequestDto> updateRequest(Set<Long> requestsId, Status status) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/requests/status method PATCH is unavailable");
    }

    @Override
    public boolean findExistRequests(Long eventId, Long userId, Status status) {
        throw new ServerUnavailableException("Endpoint api/v1/users/requests/existence method GET is unavailable");
    }
}

package ru.eventlink.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.eventlink.client.requests.RequestClient;
import ru.eventlink.client.user.UserAdminClient;
import ru.eventlink.dto.requests.ParticipationRequestDto;
import ru.eventlink.enums.Status;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Retryable(
        retryFor = {
                FeignException.class,
                SocketTimeoutException.class,
                ConnectException.class,
                IOException.class
        },
        backoff = @Backoff(delay = 500, multiplier = 2)
)
public class RestClient {
    private final UserAdminClient userAdminClient;
    private final RequestClient requestClient;

    public boolean getUserExists(long userId) {
        log.info("Calling the User Service: getUserExists({})", userId);
        return userAdminClient.getUserExists(userId);
    }

    public List<ParticipationRequestDto> findAllRequestsByEventId(long eventId, Status status) {
        log.info("Calling the Request Service: findAllRequestsByEventId({}, {})", eventId, status);
        return requestClient.findAllRequestsByEventId(eventId, status);
    }

    public List<ParticipationRequestDto> findAllRequestsByRequestsId(Set<Long> requestsId) {
        log.info("Calling the Request Service: findAllRequestsByRequestsId({})", requestsId);
        return requestClient.findAllRequestsByRequestsId(requestsId);
    }

    public List<ParticipationRequestDto> updateRequest(Set<Long> requestsId, Status status) {
        log.info("Calling the Request Service: updateRequest({}, {})", requestsId, status);
        return requestClient.updateRequest(requestsId, status);
    }
}

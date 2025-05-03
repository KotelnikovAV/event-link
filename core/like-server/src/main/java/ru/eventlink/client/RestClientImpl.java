package ru.eventlink.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.eventlink.client.event.EventAdminClient;
import ru.eventlink.client.requests.RequestClient;
import ru.eventlink.client.user.UserAdminClient;
import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.enums.Status;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

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
public class RestClientImpl implements RestClient {
    private final UserAdminClient userAdminClient;
    private final EventAdminClient eventAdminClient;
    private final RequestClient requestClient;

    @Override
    public boolean getUserExists(long userId) {
        log.info("Calling the User Service: getUserExists({})", userId);
        return userAdminClient.getUserExists(userId);
    }

    @Override
    public EventFullDto findEventById(long eventId) {
        log.info("Calling the Event Service: findEventById({})", eventId);
        return eventAdminClient.findEventById(eventId);
    }

    @Override
    public boolean findExistRequests(long eventId, long userId, Status status) {
        log.info("Calling the Request Service: findExistRequests({}, {}, {})", eventId, userId, status);
        return requestClient.findExistRequests(eventId, userId, status);
    }

    @Override
    public void updateRatingUser(long userId, int difference) {
        log.info("Calling the User Service: updateRatingUser({}, {})", userId, difference);
        userAdminClient.updateRatingUser(userId, difference);
    }

    @Override
    public void updateRatingEvent(long eventId, int difference) {
        log.info("Calling the Event Service: updateRatingEvent({}, {})", eventId, difference);
        eventAdminClient.updateRatingEvent(eventId, difference);
    }
}

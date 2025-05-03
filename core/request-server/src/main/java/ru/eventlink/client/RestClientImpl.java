package ru.eventlink.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.eventlink.client.event.EventAdminClient;
import ru.eventlink.client.user.UserAdminClient;
import ru.eventlink.dto.event.EventFullDto;

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
    public boolean findExistEventByEventIdAndInitiatorId(long eventId, long userId) {
        log.info("Calling the Event Service: findExistEventByEventIdAndInitiatorId({}, {})", eventId, userId);
        return eventAdminClient.findExistEventByEventIdAndInitiatorId(eventId, userId);
    }
}

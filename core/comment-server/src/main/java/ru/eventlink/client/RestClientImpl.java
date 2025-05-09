package ru.eventlink.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.eventlink.client.event.EventAdminClient;
import ru.eventlink.client.user.UserAdminClient;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.exception.NotFoundException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Retryable(
        retryFor = {
                FeignException.class,
                SocketTimeoutException.class,
                ConnectException.class,
        },
        noRetryFor = {
                NotFoundException.class,
        },
        backoff = @Backoff(delay = 500, multiplier = 2)
)
public class RestClientImpl implements RestClient {
    private final UserAdminClient userAdminClient;
    private final EventAdminClient eventAdminClient;

    @Override
    public boolean getEventExists(Long eventId) {
        log.info("Calling the Event Service");
        return eventAdminClient.findExistEventByEventId(eventId);
    }

    @Override
    public List<UserDto> getAllUsers(List<Long> usersId, int page, int pageSize) {
        log.info("Calling the User Service: getAllUsers({}, {}, {})", usersId, page, pageSize);
        return userAdminClient.getAllUsers(usersId, page, pageSize);
    }

    @Override
    public boolean getUserExists(long userId) {
        log.info("Calling the User Service: getUserExists({})", userId);
        return userAdminClient.getUserExists(userId);
    }
}

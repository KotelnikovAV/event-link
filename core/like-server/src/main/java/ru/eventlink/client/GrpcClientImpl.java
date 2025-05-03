package ru.eventlink.client;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.eventlink.stats.proto.ActionTypeProto;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
@Retryable(
        retryFor = {
                StatusRuntimeException.class,
                SocketTimeoutException.class,
                ConnectException.class,
                IOException.class
        },
        backoff = @Backoff(delay = 500, multiplier = 2)
)
public class GrpcClientImpl implements GrpcClient {
    private final UserActionClient userActionClient;

    @Override
    public void collectUserAction(long eventId, long userId, ActionTypeProto actionType) {
        log.info("Calling the User Action Service: collectUserAction({}, {}, {})", userId, eventId, actionType);
        userActionClient.collectUserAction(eventId, userId, actionType);
    }
}

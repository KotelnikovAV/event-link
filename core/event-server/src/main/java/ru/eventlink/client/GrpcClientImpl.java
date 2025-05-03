package ru.eventlink.client;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.eventlink.stats.proto.ActionTypeProto;
import ru.eventlink.stats.proto.RecommendedEventProto;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

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
    private final RecommendationsClient recommendationsClient;
    private final UserActionClient userActionClient;


    @Override
    public List<RecommendedEventProto> getInteractionsCount(List<Long> eventsId) {
        log.info("Calling the Recommendation Service: getInteractionsCount({})", eventsId);
        return recommendationsClient.getInteractionsCount(eventsId);
    }

    @Override
    public List<RecommendedEventProto> getRecommendationsForUser(long userId, int size) {
        log.info("Calling the Recommendation Service: getRecommendationsForUser({}, {})", userId, size);
        return recommendationsClient.getRecommendationsForUser(userId, size);
    }

    @Override
    public void collectUserAction(long userId, long eventId, ActionTypeProto actionType) {
        log.info("Calling the User Action Service: collectUserAction({}, {}, {})", userId, eventId, actionType);
        userActionClient.collectUserAction(userId, eventId, actionType);
    }
}

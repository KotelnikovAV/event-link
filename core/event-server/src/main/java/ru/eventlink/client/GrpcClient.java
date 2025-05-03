package ru.eventlink.client;

import ru.eventlink.stats.proto.ActionTypeProto;
import ru.eventlink.stats.proto.RecommendedEventProto;

import java.util.List;

public interface GrpcClient {
    List<RecommendedEventProto> getInteractionsCount(List<Long> eventsId);

    List<RecommendedEventProto> getRecommendationsForUser(long userId, int size);

    void collectUserAction(long userId, long eventId, ActionTypeProto actionType);
}

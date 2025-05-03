package ru.eventlink.client;

import ru.eventlink.stats.proto.ActionTypeProto;

public interface GrpcClient {
    void collectUserAction(long eventId, long userId, ActionTypeProto actionType);
}

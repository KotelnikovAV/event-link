package ru.eventlink.client;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.eventlink.stats.proto.ActionTypeProto;
import ru.eventlink.stats.proto.UserActionControllerGrpc;
import ru.eventlink.stats.proto.UserActionProto;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserActionClient {
    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub userActionClient;

    public void collectUserAction(long userId, long eventId, ActionTypeProto actionType) {
        UserActionProto userActionProto = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(Timestamp.newBuilder()
                        .setNanos(Instant.now().getNano())
                        .setSeconds(Instant.now().getEpochSecond())
                        .build())
                .build();

        Empty empty = userActionClient.collectUserAction(userActionProto);
    }
}

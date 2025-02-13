package ru.eventlink.user_actions.controller;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.eventlink.stats.proto.UserActionControllerGrpc;
import ru.eventlink.stats.proto.UserActionProto;
import ru.eventlink.user_actions.producer.UserActionProducer;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CollectorController extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final UserActionProducer userActionProducer;

    @Override
    public void collectUserAction(UserActionProto userActionProto, StreamObserver<Empty> responseObserver) {
        log.info("Collect user action");

        userActionProducer.collectUserAction(userActionProto);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}

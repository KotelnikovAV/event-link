package ru.eventlink.events_similarity.controller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.eventlink.events_similarity.service.EventSimilarityService;
import ru.eventlink.stats.proto.*;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final EventSimilarityService eventSimilarityService;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto userPredictionsRequestProto,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("get recommendations for user");

        eventSimilarityService.getRecommendationsForUser(userPredictionsRequestProto)
                .forEach(responseObserver::onNext);

        responseObserver.onCompleted();
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto similarEventsRequestProto,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("get similar events");

        eventSimilarityService.getSimilarEvents(similarEventsRequestProto)
                .forEach(responseObserver::onNext);

        responseObserver.onCompleted();
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto similarEventsRequestProto,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("get interactions count");

        eventSimilarityService.getInteractionsCount(similarEventsRequestProto)
                .forEach(responseObserver::onNext);

        responseObserver.onCompleted();
    }
}

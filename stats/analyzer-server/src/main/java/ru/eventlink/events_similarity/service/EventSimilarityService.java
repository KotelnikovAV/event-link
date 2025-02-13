package ru.eventlink.events_similarity.service;

import ru.eventlink.stats.avro.EventSimilarityAvro;
import ru.eventlink.stats.proto.InteractionsCountRequestProto;
import ru.eventlink.stats.proto.RecommendedEventProto;
import ru.eventlink.stats.proto.SimilarEventsRequestProto;
import ru.eventlink.stats.proto.UserPredictionsRequestProto;

import java.util.List;

public interface EventSimilarityService {
    void saveEventsSimilarity(List<EventSimilarityAvro> eventsSimilarityAvro);

    List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto similarEventsRequestProto);

    List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto interactionsCountRequestProto);

    List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto userPredictionsRequestProto);
}

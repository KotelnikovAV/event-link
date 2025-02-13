package ru.eventlink.events_similarity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.eventlink.events_similarity.model.EventSimilarity;
import ru.eventlink.events_similarity.model.EventSimilarityId;
import ru.eventlink.stats.avro.EventSimilarityAvro;
import ru.eventlink.stats.proto.RecommendedEventProto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class EventSimilarityMapper {
    @Mapping(target = "eventSimilarityId", expression = "java(getEventSimilarityId(eventSimilarityAvro.getEventA(), eventSimilarityAvro.getEventB()))")
    public abstract EventSimilarity eventSimilarityAvroToEventSimilarity(EventSimilarityAvro eventSimilarityAvro);

    public abstract List<EventSimilarity> listEventSimilarityAvroToListEventSimilarity(List<EventSimilarityAvro> eventsSimilarityAvro);

    public RecommendedEventProto eventSimilarityToRecommendedEventProto(EventSimilarity eventSimilarity) {
        return RecommendedEventProto.newBuilder()
                .setEventId(eventSimilarity.getEventSimilarityId().getEventB())
                .setScore(eventSimilarity.getScore())
                .build();
    }

    public EventSimilarityId getEventSimilarityId(Long eventA, Long eventB) {
        return new EventSimilarityId(eventA, eventB);
    }

    public abstract List<RecommendedEventProto> listEventSimilarityToListRecommendedEventProto(List<EventSimilarity> eventSimilarities);
}

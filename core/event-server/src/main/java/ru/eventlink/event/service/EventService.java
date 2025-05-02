package ru.eventlink.event.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import ru.eventlink.client.GrpcClient;
import ru.eventlink.enums.EventPublicSort;
import ru.eventlink.event.model.Event;
import ru.eventlink.stats.proto.RecommendedEventProto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class EventService {
    protected final GrpcClient grpcClient;

    public EventService(GrpcClient grpcClient) {
        this.grpcClient = grpcClient;
    }

    protected PageRequest getCustomPage(int page, int size, EventPublicSort sort) {
        if (sort != null) {
            return switch (sort) {
                case EVENT_DATE -> PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "eventDate"));
                case VIEWS -> PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "views"));
                case LIKES -> PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likes"));
            };
        } else {
            return PageRequest.of(page, size);
        }

    }

    protected void setRating(List<Event> events) {
        if (CollectionUtils.isEmpty(events)) {
            return;
        }

        List<Long> eventsId = events.stream()
                .map(Event::getId)
                .toList();

        List<RecommendedEventProto> ratingEvents = grpcClient.getInteractionsCount(eventsId);

        if (CollectionUtils.isEmpty(ratingEvents)) {
            return;
        }

        Map<Long, Double> mapEventsIdRating = ratingEvents.stream()
                .collect(Collectors.toMap(RecommendedEventProto::getEventId, RecommendedEventProto::getScore));

        events.forEach(event -> event.setRating(mapEventsIdRating.getOrDefault(event.getId(), 0.0)));
    }
}

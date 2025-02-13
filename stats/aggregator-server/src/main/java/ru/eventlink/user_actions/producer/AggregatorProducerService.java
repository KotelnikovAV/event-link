package ru.eventlink.user_actions.producer;

import ru.eventlink.stats.avro.EventSimilarityAvro;

public interface AggregatorProducerService {
    void aggregateUserActions(EventSimilarityAvro message);
}

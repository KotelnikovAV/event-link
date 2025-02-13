package ru.eventlink.user_actions.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import ru.eventlink.stats.avro.EventSimilarityAvro;
import ru.eventlink.stats.avro.UserActionAvro;

import java.util.List;

public interface AggregatorService {
    List<EventSimilarityAvro> handleRecord(ConsumerRecord<Void, UserActionAvro> record);
}

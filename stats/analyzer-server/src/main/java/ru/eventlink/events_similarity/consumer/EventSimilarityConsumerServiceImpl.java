package ru.eventlink.events_similarity.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.eventlink.configuration.ConsumerEventsSimilarityConfig;
import ru.eventlink.events_similarity.service.EventSimilarityService;
import ru.eventlink.stats.avro.EventSimilarityAvro;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class EventSimilarityConsumerServiceImpl implements EventSimilarityConsumerService {
    private final ConsumerEventsSimilarityConfig consumerConfig;
    private final EventSimilarityService eventSimilarityService;

    @Override
    public void consumeEventsSimilarity() {
        Consumer<Void, EventSimilarityAvro> consumer = consumerConfig.getConsumer();
        List<EventSimilarityAvro> eventsSimilarityAvro = new ArrayList<>();

        try (consumer) {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(consumerConfig.getTopics().get("events_similarity")));
            while (true) {
                ConsumerRecords<Void, EventSimilarityAvro> records = consumer
                        .poll(consumerConfig.getConsumeTimeout());

                for (ConsumerRecord<Void, EventSimilarityAvro> record : records) {
                    eventsSimilarityAvro.add(record.value());
                }

                eventSimilarityService.saveEventsSimilarity(eventsSimilarityAvro);
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {

        }
    }
}

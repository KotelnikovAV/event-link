package ru.eventlink.user_actions.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.eventlink.configuration.ConsumerConfig;
import ru.eventlink.stats.avro.EventSimilarityAvro;
import ru.eventlink.stats.avro.UserActionAvro;
import ru.eventlink.user_actions.producer.AggregatorProducerService;
import ru.eventlink.user_actions.service.AggregatorService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorConsumerServiceImpl implements AggregatorConsumerService {
    private final ConsumerConfig consumerConfig;
    private final AggregatorProducerService aggregatorProducerService;
    private final AggregatorService aggregatorService;

    @Override
    public void consumeUserActions() {
        Consumer<Void, UserActionAvro> consumer = consumerConfig.getConsumer();

        try (consumer) {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(consumerConfig.getTopics().get("user_actions")));
            while (true) {
                ConsumerRecords<Void, UserActionAvro> records = consumer
                        .poll(consumerConfig.getConsumeTimeout());

                for (ConsumerRecord<Void, UserActionAvro> record : records) {
                    List<EventSimilarityAvro> eventsSimilarityAvro = aggregatorService.handleRecord(record);
                    eventsSimilarityAvro.forEach(aggregatorProducerService::aggregateUserActions);
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        }
    }
}

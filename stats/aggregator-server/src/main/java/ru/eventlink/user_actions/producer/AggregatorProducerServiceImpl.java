package ru.eventlink.user_actions.producer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.eventlink.configuration.ProducerConfig;
import ru.eventlink.stats.avro.EventSimilarityAvro;
import ru.eventlink.user_actions.utility.ProducerActivityTimer;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregatorProducerServiceImpl implements AggregatorProducerService {
    private final Object monitor = new Object();

    private final ProducerConfig producerConfig;
    private Producer<String, SpecificRecordBase> producer;

    @Override
    public void aggregateUserActions(EventSimilarityAvro message) {

        synchronized (monitor) {
            initializeProducerAndRunActivityTimer();

            ProducerRecord<String, SpecificRecordBase> record =
                    new ProducerRecord<>(producerConfig.getTopics().get("events-similarity"), message);

            producer.send(record);
        }
    }

    private void initializeProducerAndRunActivityTimer() {
        if (ProducerActivityTimer.getNecessityNewProducer()) {
            producer = producerConfig.getKafkaProducer();
        }

        ProducerActivityTimer.startActivityTimerProducer(producer,
                producerConfig.getTimeUntilClosingKafkaProducerMs(),
                monitor);
    }

    @PreDestroy
    private void interruptThreadForTrackingProducerActivityAndCloseProducer() {
        ProducerActivityTimer.stopActivityTimerProducer();

        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}

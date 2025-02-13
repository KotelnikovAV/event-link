package ru.eventlink.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties("collector.kafka.producer")
public class ProducerConfig {
    private final Map<String, String> properties;
    private final Map<String, String> topics;
    private final long timeUntilClosingKafkaProducerMs;

    public Producer<String, SpecificRecordBase> getKafkaProducer() {
       return new KafkaProducer<>(getPropertiesForKafkaProducer());
    }

    private Properties getPropertiesForKafkaProducer() {
        Properties config = new Properties();
        config.putAll(properties);
        return config;
    }
}
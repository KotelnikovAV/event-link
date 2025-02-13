package ru.eventlink.user_action.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.eventlink.configuration.ConsumerUserActionsConfig;
import ru.eventlink.stats.avro.UserActionAvro;
import ru.eventlink.user_action.service.UserActionService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionConsumerServiceImpl implements UserActionConsumerService {
    private final ConsumerUserActionsConfig consumerConfig;
    private final UserActionService userActionsService;

    @Override
    public void consumeUserActions() {
        Consumer<Void, UserActionAvro> consumer = consumerConfig.getConsumer();

        try (consumer) {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(consumerConfig.getTopics().get("user_actions")));
            while (true) {
                ConsumerRecords<Void, UserActionAvro> records = consumer
                        .poll(consumerConfig.getConsumeTimeout());
                List<UserActionAvro> userActionAvro = new ArrayList<>();

                for (ConsumerRecord<Void, UserActionAvro> record : records) {
                    userActionAvro.add(record.value());
                }

                userActionsService.saveUserActions(userActionAvro);

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {

        }
    }
}

package ru.eventlink.user_action.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.eventlink.user_action.consumer.UserActionConsumerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerUserActionStarter {
    private final UserActionConsumerService userActionConsumerService;

    public void start() {
        log.info("Starting aggregator consumer service");
        userActionConsumerService.consumeUserActions();
    }
}

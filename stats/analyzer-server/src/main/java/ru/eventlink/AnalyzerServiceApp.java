package ru.eventlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.eventlink.events_similarity.starter.AnalyzerEventSimilarityStarter;
import ru.eventlink.user_action.starter.AnalyzerUserActionStarter;

@SpringBootApplication
@ConfigurationPropertiesScan("ru.eventlink.configuration")
public class AnalyzerServiceApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerServiceApp.class, args);
        AnalyzerUserActionStarter analyzerUserAction = context.getBean(AnalyzerUserActionStarter.class);
        analyzerUserAction.start();
        AnalyzerEventSimilarityStarter analyzerEventsSimilarity = context.getBean(AnalyzerEventSimilarityStarter.class);
        analyzerEventsSimilarity.start();
    }
}

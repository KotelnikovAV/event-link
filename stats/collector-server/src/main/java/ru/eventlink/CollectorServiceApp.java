package ru.eventlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("ru.eventlink.configuration")
public class CollectorServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CollectorServiceApp.class, args);
    }
}

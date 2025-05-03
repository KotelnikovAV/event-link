package ru.eventlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
@ConfigurationPropertiesScan("ru.eventlink.configuration")
public class CommentServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CommentServiceApp.class, args);
    }
}

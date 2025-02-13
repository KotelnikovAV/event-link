package ru.eventlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LikeServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(LikeServiceApp.class, args);
    }
}

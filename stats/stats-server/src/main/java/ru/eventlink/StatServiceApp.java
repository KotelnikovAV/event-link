package ru.eventlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StatServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(StatServiceApp.class, args);
    }
}

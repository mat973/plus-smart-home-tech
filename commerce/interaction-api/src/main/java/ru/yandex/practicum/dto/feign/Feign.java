package ru.yandex.practicum.dto.feign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Feign {
    public static void main(String[] args) {
        SpringApplication.run(Feign.class);
    }
}

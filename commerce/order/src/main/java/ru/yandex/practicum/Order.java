package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.dto.feign.client.CartClient;


@SpringBootApplication
@EnableFeignClients(clients = {CartClient.class})
public class Order {
    public static void main(String[] args) {
        SpringApplication.run(Order.class, args);
    }
}

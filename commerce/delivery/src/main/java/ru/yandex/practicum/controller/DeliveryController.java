package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {
   private final DeliveryService service;
    @PutMapping
    public DeliveryDto createOrder(@Valid @RequestBody DeliveryDto deliveryDto){
        log.info("Запрос на создание доставки {}", deliveryDto);
        return service.createOrder(deliveryDto);
    }
    @PostMapping("/successful")
    public void successfulDelivery(@RequestBody UUID deliveryId){
        service.successfulDelivery(deliveryId);
    }

    @PostMapping("/picked")
    public void pickedDelivery(@RequestBody UUID deliveryId){
        service.pickedDelivery(deliveryId);
    }

    @PostMapping("/failed")
    public void failedDelivery(@RequestBody UUID deliveryId){
        service.failedDelivery(deliveryId);
    }

    @PostMapping("/cost")
    public BigDecimal costDelivery(@Valid @RequestBody OrderDto orderDto){
        return service.costDelivery(orderDto);
    }
}

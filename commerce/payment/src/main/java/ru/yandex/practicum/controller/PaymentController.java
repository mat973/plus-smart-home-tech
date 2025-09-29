package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService service;

    @PostMapping
    public PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto) {
        log.info("Запрос на Формирование оплаты для заказа : {}", orderDto);
        return service.createPayment(orderDto);
    }

    @PostMapping("/totalCost")
    public BigDecimal totalCost(@RequestBody @Valid OrderDto orderDto) {
        log.info("Запрос Расчёт полной стоимости заказа {}", orderDto);
        return service.totalCost(orderDto);
    }

    @PostMapping("/refund")
    public void refundOrder(@RequestBody UUID paymentId) {
        log.info("Метод для эмуляции успешной оплаты в платежного шлюза для заказа с Id: {}", paymentId);
        service.refundOrder(paymentId);
    }

    @PostMapping("/productCost")
    public BigDecimal calculateOrderTotal(@RequestBody @Valid OrderDto orderDto) {
        log.info("Запрос неа расчет стоимости товаров в заказе : {}", orderDto);
        return service.calculateOrderTotal(orderDto);
    }

    @PostMapping("/failed")
    public void failedPayment(@RequestBody UUID paymentId) {
        log.info("Метод для эмуляции отказа в оплате платежного шлюза для заказа с Id: {}", paymentId);
        service.failedPayment(paymentId);
    }

}

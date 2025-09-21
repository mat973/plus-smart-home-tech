package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
@Slf4j
@Validated
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getClientOrders(
            @RequestParam
            @NotBlank(message = "Имя пользователя не должно быть пустым") String username) {
        log.info("Выполняется запрос: GET /api/v3/order | username={}", username);
        return orderService.getClientOrders(username);
    }

    @PutMapping
    public OrderDto createNewOrder(@RequestBody @Valid CreateNewOrderRequest request) {
        log.info("Выполняется запрос: PUT /api/v3/order | request={}", request);
        return orderService.createNewOrder(request);
    }

    @PostMapping("/return")
    public OrderDto productReturn(@RequestBody @Valid ProductReturnRequest request) {
        log.info("Выполняется запрос: POST /api/v3/order/return | request={}", request);
        return orderService.productReturn(request);
    }

    @PostMapping("/payment")
    public OrderDto payment(@RequestBody @NotNull(message = "orderId обязателен") UUID orderId) {
        log.info("Выполняется запрос: POST /api/v3/order/payment | orderId={}", orderId);
        return orderService.payment(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(@RequestBody @NotNull(message = "orderId обязателен") UUID orderId) {
        log.info("Выполняется запрос: POST /api/v3/order/payment/failed | orderId={}", orderId);
        return orderService.paymentFailed(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto delivery(@RequestBody @NotNull(message = "orderId обязателен") UUID orderId) {
        log.info("Выполняется запрос: POST /api/v3/order/delivery | orderId={}", orderId);
        return orderService.delivery(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(@RequestBody @NotNull(message = "orderId обязателен") UUID orderId) {
        log.info("Выполняется запрос: POST /api/v3/order/delivery/failed | orderId={}", orderId);
        return orderService.deliveryFailed(orderId);
    }

    @PostMapping("/completed")
    public OrderDto complete(@RequestBody @NotNull(message = "orderId обязателен") UUID orderId) {
        log.info("Выполняется запрос: POST /api/v3/order/completed | orderId={}", orderId);
        return orderService.complete(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalCost(@RequestBody @NotNull(message = "orderId обязателен") UUID orderId) {
        log.info("Выполняется запрос: POST /api/v3/order/calculate/total | orderId={}", orderId);
        return orderService.calculateTotalCost(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryCost(@RequestBody @NotNull(message = "orderId обязателен") UUID orderId) {
        log.info("Выполняется запрос: POST /api/v3/order/calculate/delivery | orderId={}", orderId);
        return orderService.calculateDeliveryCost(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assembly(@RequestBody @NotNull(message = "orderId обязателен") UUID orderId) {
        log.info("Выполняется запрос: POST /api/v3/order/assembly | orderId={}", orderId);
        return orderService.assembly(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(@RequestBody @NotNull(message = "orderId обязателен") UUID orderId) {
        log.info("Выполняется запрос: POST /api/v3/order/assembly/failed | orderId={}", orderId);
        return orderService.assemblyFailed(orderId);
    }
}

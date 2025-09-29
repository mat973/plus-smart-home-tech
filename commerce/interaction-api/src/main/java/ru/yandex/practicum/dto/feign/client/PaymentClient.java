package ru.yandex.practicum.dto.feign.client;


import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "PAYMENT")
public interface PaymentClient {
    @PostMapping("/api/v1/payment")
    PaymentDto createPayment(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/payment/totalCost")
    BigDecimal totalCost(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/payment/refund")
    void refundOrder(@RequestBody UUID paymentId);

    @PostMapping("/api/v1/payment/productCost")
    BigDecimal calculateOrderTotal(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/api/v1/payment/failed")
    void failedPayment(@RequestBody UUID paymentId);
}

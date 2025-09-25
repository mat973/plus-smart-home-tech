package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.feign.client.CartClient;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CartClient cartClient;

    public List<OrderDto> getClientOrders(String username) {
        ShoppingCartDto cartDto = cartClient.getCart(username);
        return repository.findAllByuserName(username)...
    }

    public OrderDto createNewOrder(CreateNewOrderRequest request) {
    }

    public OrderDto productReturn(ProductReturnRequest request) {
    }

    public OrderDto payment(UUID orderId) {
    }

    public OrderDto paymentFailed(UUID orderId) {
    }

    public OrderDto delivery(UUID orderId) {
    }

    public OrderDto deliveryFailed(UUID orderId) {
    }

    public OrderDto complete(UUID orderId) {
    }

    public OrderDto calculateTotalCost(UUID orderId) {
    }

    public OrderDto calculateDeliveryCost(UUID orderId) {
    }

    public OrderDto assembly(UUID orderId) {
    }

    public OrderDto assemblyFailed(UUID orderId) {
    }
}

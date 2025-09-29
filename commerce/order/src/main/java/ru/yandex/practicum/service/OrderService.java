package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.feign.client.CartClient;
import ru.yandex.practicum.dto.feign.client.DeliveryClient;
import ru.yandex.practicum.dto.feign.client.PaymentClient;
import ru.yandex.practicum.dto.feign.client.WarehouseClient;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.order.OrderStateDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exceptions.NoOrderFoundException;
import ru.yandex.practicum.exceptions.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartClient shoppingCartClient;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    private static final String MESSAGE_ORDER_NOT_FOUND = "Заказ не найден.";

    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username, Integer page, Integer size) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым.");
        }
        ShoppingCartDto shoppingCart = shoppingCartClient.getCart(username);

        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(page, size, sortByCreated);
        List<Order> orders = orderRepository.findByShoppingCartId(
                shoppingCart.getShoppingCartId(), pageRequest);
        return orders.stream().map(orderMapper::toOrderDto).toList();
    }

    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        // Сохраняем заказ с товарами из корзины
        Order order = Order.builder()
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .orderStateDto(OrderStateDto.NEW)
                .build();
        Order savedOrder = orderRepository.save(order);


        BookedProductsDto bookedProducts = warehouseClient.assemblyProductsForOrder(
                new AssemblyProductsForOrderRequest(
                        request.getShoppingCart().getProducts(),
                        savedOrder.getOrderId()
                )
        );

        savedOrder.setFragile(bookedProducts.getFragile());
        savedOrder.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        savedOrder.setDeliveryWeight(bookedProducts.getDeliveryWight());


        savedOrder.setProductPrice(paymentClient.calculateOrderTotal(orderMapper.toOrderDto(savedOrder)));


        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(savedOrder.getOrderId())
                .fromAddress(warehouseClient.getCurrentWarehouseAddress())
                .toAddress(request.getDeliveryAddress())
                .build();
        savedOrder.setDeliveryId(deliveryClient.createOrder(deliveryDto).getDeliveryId());

        paymentClient.createPayment(orderMapper.toOrderDto(savedOrder));

        return orderMapper.toOrderDto(savedOrder);
    }

    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException(MESSAGE_ORDER_NOT_FOUND));

        warehouseClient.returnedProduct(request.getProducts());
        order.setOrderStateDto(OrderStateDto.PRODUCT_RETURNED);

        return orderMapper.toOrderDto(order);
    }

    public OrderDto payment(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.PAID);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto paymentFailed(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.PAYMENT_FAILED);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto delivery(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.DELIVERED);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto deliveryFailed(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.DELIVERY_FAILED);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto complete(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.COMPLETED);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        order.setTotalPrice(paymentClient.totalCost(orderMapper.toOrderDto(order)));
        return orderMapper.toOrderDto(order);
    }

    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        order.setDeliveryPrice(deliveryClient.costDelivery(orderMapper.toOrderDto(order)));
        return orderMapper.toOrderDto(order);
    }

    public OrderDto assembly(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.ASSEMBLED);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto assemblyFailed(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.ASSEMBLY_FAILED);
        return orderMapper.toOrderDto(order);
    }


    private Order findOrderOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(MESSAGE_ORDER_NOT_FOUND));
    }
}


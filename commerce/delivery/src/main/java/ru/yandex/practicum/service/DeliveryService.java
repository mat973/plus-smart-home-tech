package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.feign.client.OrderClient;
import ru.yandex.practicum.dto.feign.client.WarehouseClient;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.model.DeliveryState;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private static final BigDecimal DEFAULT_COST = new BigDecimal("5");
    private static final BigDecimal WAREHOUSE_ADDRESS_MULTIPLE = new BigDecimal("2");
    private static final BigDecimal FRAGILE_MULTIPLE = new BigDecimal("0.2");
    private static final BigDecimal DELIVERY_WEIGHT_MULTIPLE = new BigDecimal("0.3");
    private static final BigDecimal DELIVERY_VOLUME_MULTIPLE = new BigDecimal("0.3");
    private static final BigDecimal DELIVERY_DISTANCE_MULTIPLE = new BigDecimal("0.2");
    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;

    @Transactional
    public DeliveryDto createOrder(DeliveryDto deliveryDto) {
        return mapper.toDeliveryDto(repository.save(mapper.toDelivery(deliveryDto)));
    }

    @Transactional
    public void successfulDelivery(UUID deliveryId) {
        Delivery delivery = repository.findById(deliveryId).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        orderClient.complete(delivery.getOrderId());
    }

    @Transactional
    public void pickedDelivery(UUID deliveryId) {
        Delivery delivery = repository.findById(deliveryId).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        orderClient.assembly(delivery.getOrderId());
        ShippedToDeliveryRequest deliveryRequest = new ShippedToDeliveryRequest(
                delivery.getOrderId(), delivery.getDeliveryId());
        warehouseClient.shippedToDelivery(deliveryRequest);
    }

    @Transactional
    public void failedDelivery(UUID deliveryId) {
        Delivery delivery = repository.findById(deliveryId).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.assemblyFailed(delivery.getOrderId());
    }

    public BigDecimal costDelivery(OrderDto orderDto) {
        Delivery delivery = repository.findById(orderDto.getDeliveryId())
                .orElseThrow(() -> new NotFoundException("Доставка не найдена"));
        BigDecimal cost = DEFAULT_COST;
        AddressDto warehouseAddress = warehouseClient.getCurrentWarehouseAddress();
        if ("ADDRESS_2".equals(warehouseAddress.getCity())) {
            cost = cost.add(cost.multiply(WAREHOUSE_ADDRESS_MULTIPLE));
        }
        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            cost = cost.add(cost.multiply(FRAGILE_MULTIPLE));
        }

        cost = cost.add(BigDecimal.valueOf(orderDto.getDeliveryWeight()).multiply(DELIVERY_WEIGHT_MULTIPLE));

        cost = cost.add(BigDecimal.valueOf(orderDto.getDeliveryVolume()).multiply(DELIVERY_VOLUME_MULTIPLE));

        if (warehouseAddress.getCity().equals(delivery.getToAddress().getCity())
                && warehouseAddress.getStreet().equals(delivery.getToAddress().getStreet())) {
            return cost;
        } else {
            return cost.add(cost.multiply(DELIVERY_DISTANCE_MULTIPLE));
        }
    }
}

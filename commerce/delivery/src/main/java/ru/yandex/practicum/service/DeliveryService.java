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
import ru.yandex.practicum.model.State;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {
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
        delivery.setDeliveryState(State.DELIVERED);
        orderClient.complete(delivery.getOrderId());
    }

    @Transactional
    public void pickedDelivery(UUID deliveryId) {
        Delivery delivery = repository.findById(deliveryId).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(State.IN_PROGRESS);
        orderClient.assembly(delivery.getOrderId());
        ShippedToDeliveryRequest deliveryRequest = new ShippedToDeliveryRequest(
                delivery.getOrderId(), delivery.getDeliveryId());
        warehouseClient.shippedToDelivery(deliveryRequest);
    }
    @Transactional
    public void failedDelivery(UUID deliveryId) {
        Delivery delivery = repository.findById(deliveryId).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(State.FAILED);
        orderClient.assemblyFailed(delivery.getOrderId());
    }

    public BigDecimal costDelivery(OrderDto orderDto) {
        Delivery delivery = repository.findById(orderDto.getDeliveryId())
                .orElseThrow(() -> new NotFoundException("Доставка не найдена"));
        BigDecimal cost = new BigDecimal("5.0");
        AddressDto warehouseAddress = warehouseClient.getCurrentWarehouseAddress();
        if ("ADDRESS_2".equals(warehouseAddress.getCity())) {
            cost = cost.add(cost.multiply(new BigDecimal("2")));
        }
//        else {
//            cost = cost.add(cost.multiply(new BigDecimal("1")));
//        }
        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            cost = cost.add(cost.multiply(new BigDecimal("0.2")));
        }

        cost = cost.add(BigDecimal.valueOf(orderDto.getDeliveryWeight()).multiply(BigDecimal.valueOf(0.3)));

        cost = cost.add(BigDecimal.valueOf(orderDto.getDeliveryVolume()).multiply(BigDecimal.valueOf(0.2)));

        if (warehouseAddress.getCity().equals(delivery.getToAddress().getCity())
                && warehouseAddress.getStreet().equals(delivery.getToAddress().getStreet())){
            return cost;
        }else {
            return cost.add(cost.multiply(BigDecimal.valueOf(0.2)));
        }
    }
}

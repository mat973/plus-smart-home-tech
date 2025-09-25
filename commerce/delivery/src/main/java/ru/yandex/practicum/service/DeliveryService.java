package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
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

    @Transactional
    public DeliveryDto createOrder(DeliveryDto deliveryDto) {
        return mapper.toDeliveryDto(repository.save(mapper.toDelivery(deliveryDto)));
    }


    @Transactional
    public void successfulDelivery(UUID deliveryId) {
        Delivery delivery = repository.findById(deliveryId).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(State.DELIVERED);
    }

    @Transactional
    public void pickedDelivery(UUID deliveryId) {
        Delivery delivery = repository.findById(deliveryId).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(State.IN_PROGRESS);
    }
    @Transactional
    public void failedDelivery(UUID deliveryId) {
        Delivery delivery = repository.findById(deliveryId).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(State.FAILED;
    }

    public BigDecimal costDelivery(@Valid OrderDto orderDto) {
    }
}

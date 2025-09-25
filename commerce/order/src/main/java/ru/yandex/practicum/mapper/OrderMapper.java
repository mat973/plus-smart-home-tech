package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.model.Order;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface OrderMapper {
    Order toOrder(OrderDto orderDto);
    OrderDto toORderDto(Order order);
}

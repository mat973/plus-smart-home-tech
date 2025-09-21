package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public class ProductReturnRequest {
    private UUID orderId;
    @NotNull
    private Map<UUID, Integer> products;
}

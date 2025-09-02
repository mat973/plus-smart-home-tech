package ru.yandex.practicum.dto.producte;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;
    @NotNull(message = "Состояние остатка товара не должно быть пустое")
    private QuantityState quantityState;
}

package ru.yandex.practicum.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChangeProductQuantityRequest {
    private UUID productId;
    @NotNull
    private Long newQuantity;
}

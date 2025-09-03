package ru.yandex.practicum.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class ShoppingCartDto {
    @NotNull
    private UUID shoppingCartId;
    private Map<UUID, Long> products;
}

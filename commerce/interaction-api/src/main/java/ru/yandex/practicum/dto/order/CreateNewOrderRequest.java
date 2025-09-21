package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateNewOrderRequest {
    @NotNull(message = "Заказа не может быть без корзины")
    private ShoppingCartDto shoppingCart;
    @NotNull(message = "Доставка должна иметь адрес")
    private AddressDto deliveryAddress;
}

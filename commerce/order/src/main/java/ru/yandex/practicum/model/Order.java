package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.dto.order.OrderStateDto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    private UUID orderId;
    private UUID shoppingCartId;
    private Map<UUID, Long> products;
    private UUID paymentId;
    private UUID deliveryId;
    @Enumerated(EnumType.STRING)
    private OrderStateDto orderStateDto;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
    private BigDecimal totalPrice;
    private BigDecimal deliveryPrice;
    private BigDecimal productPrice;


}

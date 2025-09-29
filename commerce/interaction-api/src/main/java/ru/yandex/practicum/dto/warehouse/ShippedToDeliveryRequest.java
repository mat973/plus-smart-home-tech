package ru.yandex.practicum.dto.warehouse;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippedToDeliveryRequest {
    private UUID orderId;
    private UUID deliveryId;
}

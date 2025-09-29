package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReturnRequest {
    private UUID orderId;
    @NotNull
    private Map<UUID, Long> products;
}

package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AssemblyProductsForOrderRequest {
    @NotNull
    private Map<UUID, Long> products;
    @NotNull
    private UUID orderId;
}

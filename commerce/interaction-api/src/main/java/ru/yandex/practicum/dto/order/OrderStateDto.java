package ru.yandex.practicum.dto.order;

public enum OrderStateDto {
    NEW, ON_PAYMENT,
    ON_DELIVERY,
    DONE, DELIVERED,
    ASSEMBLED,
    PAID,
    COMPLETED,
    DELIVERY_FAILED,
    ASSEMBLY_FAILED,
    PAYMENT_FAILED,
    PRODUCT_RETURNED,
    CANCELED
}

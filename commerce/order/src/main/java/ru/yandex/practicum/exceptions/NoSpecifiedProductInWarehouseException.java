package ru.yandex.practicum.exceptions;

public class NoSpecifiedProductInWarehouseException extends RuntimeException {
    public NoSpecifiedProductInWarehouseException(String message) {
        super(message);
    }

    public NoSpecifiedProductInWarehouseException(String message, Throwable cause) {
        super(message, cause);
    }
}

package ru.yandex.practicum.exceptions;

public class NotAuthorizedUserException extends RuntimeException {
    public NotAuthorizedUserException(String message) {
        super(message);
    }

    public NotAuthorizedUserException(String message, Throwable cause) {
        super(message, cause);
    }
}

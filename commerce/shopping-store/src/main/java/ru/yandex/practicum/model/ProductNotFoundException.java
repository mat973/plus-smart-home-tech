package ru.yandex.practicum.model;

import java.util.List;

public class ProductNotFoundException extends RuntimeException {
    private String httpStatus;
    private String userMessage;

    public ProductNotFoundException(String message, String userMessage, String httpStatus, Throwable cause) {
        super(message, cause);
        this.userMessage = userMessage;
        this.httpStatus = httpStatus;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public String getUserMessage() {
        return userMessage;
    }


    public List<StackTraceElement> getFullStackTrace() {
        return List.of(this.getStackTrace());
    }
}

package ru.yandex.practicum.exceptions;


import ru.yandex.practicum.exceptions.ExceptionResponse.Cause;
import ru.yandex.practicum.exceptions.ExceptionResponse.StackTraceElementDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



@RestControllerAdvice
public class GlobalExceptionHandler {

    private ExceptionResponse buildResponse(RuntimeException ex) {
        List<ExceptionResponse.StackTraceElementDto> stackTrace = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElementDto::new)
                .collect(Collectors.toList());

        Cause cause = new Cause(stackTrace, ex.getMessage(), ex.getLocalizedMessage());
        return new ExceptionResponse(cause);
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<ExceptionResponse> handleNoSpecifiedProduct(NoSpecifiedProductInWarehouseException ex) {
        return new ResponseEntity<>(buildResponse(ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoOrderFound(NoOrderFoundException ex) {
        return new ResponseEntity<>(buildResponse(ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ExceptionResponse> handleNotAuthorized(NotAuthorizedUserException ex) {
        return new ResponseEntity<>(buildResponse(ex), HttpStatus.FORBIDDEN);
    }
}

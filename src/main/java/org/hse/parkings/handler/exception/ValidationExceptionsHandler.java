package org.hse.parkings.handler.exception;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.exception.EngagedException;
import org.hse.parkings.model.error.CauseMessage;
import org.hse.parkings.model.error.Error;
import org.hse.parkings.utils.DateTimeProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class ValidationExceptionsHandler {

    private final DateTimeProvider dateTimeProvider;

    @ExceptionHandler(EngagedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected Error handleRuntime(RuntimeException ex, WebRequest request) {
        return new Error(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                dateTimeProvider.getZonedDateTime(),
                Collections.singletonList(new CauseMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected Error handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new Error(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                dateTimeProvider.getZonedDateTime(),
                ex.getConstraintViolations()
                        .stream()
                        .map(e -> new CauseMessage(e.getPropertyPath().toString(), e.getMessage()))
                        .collect(Collectors.toList()),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected Error handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        return new Error(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                dateTimeProvider.getZonedDateTime(),
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(e -> new CauseMessage(e.getField(), e.getDefaultMessage()))
                        .collect(Collectors.toList()),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected Error handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        return new Error(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                dateTimeProvider.getZonedDateTime(),
                Collections.singletonList(new CauseMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected Error handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        return new Error(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                dateTimeProvider.getZonedDateTime(),
                Collections.singletonList(new CauseMessage(ex.getName(), ex.getMessage())),
                request.getDescription(false)
        );
    }
}

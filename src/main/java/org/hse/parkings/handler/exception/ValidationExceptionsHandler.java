package org.hse.parkings.handler.exception;

import org.hse.parkings.exception.EngagedException;
import org.hse.parkings.exception.ErrorMessage;
import org.hse.parkings.exception.ParamMessage;
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
public class ValidationExceptionsHandler {

    @ExceptionHandler(EngagedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleRuntime(RuntimeException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                DateTimeProvider.getInstance().getZonedDateTime(),
                Collections.singletonList(new ParamMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                DateTimeProvider.getInstance().getZonedDateTime(),
                ex.getConstraintViolations()
                        .stream()
                        .map(e -> new ParamMessage(e.getPropertyPath().toString(), e.getMessage()))
                        .collect(Collectors.toList()),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                DateTimeProvider.getInstance().getZonedDateTime(),
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(e -> new ParamMessage(e.getField(), e.getDefaultMessage()))
                        .collect(Collectors.toList()),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                DateTimeProvider.getInstance().getZonedDateTime(),
                Collections.singletonList(new ParamMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleHttpMessageNotReadable(MethodArgumentTypeMismatchException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                DateTimeProvider.getInstance().getZonedDateTime(),
                Collections.singletonList(new ParamMessage(ex.getName(), ex.getMessage())),
                request.getDescription(false)
        );
    }
}

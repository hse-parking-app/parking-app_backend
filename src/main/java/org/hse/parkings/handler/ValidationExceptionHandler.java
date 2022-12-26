package org.hse.parkings.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.hse.parkings.exception.EngagedException;
import org.hse.parkings.exception.ErrorMessage;
import org.hse.parkings.exception.ParamMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler({
            EngagedException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleRuntime(RuntimeException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                Collections.singletonList(new ParamMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }

    @ExceptionHandler({
            ConstraintViolationException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getConstraintViolations()
                        .stream()
                        .map(e -> new ParamMessage(
                                e.getRootBeanClass().getName() + " " + e.getPropertyPath(),
                                e.getMessage())
                        )
                        .collect(Collectors.toList()),
                request.getDescription(false)
        );
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(e -> new ParamMessage(e.getField(), e.getDefaultMessage()))
                        .collect(Collectors.toList()),
                request.getDescription(false)
        );
    }

    @ExceptionHandler({
            InvalidFormatException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleIllegalState(InvalidFormatException ex, WebRequest request) {
        if (ex.getTargetType().equals(UUID.class)) {
            return new ErrorMessage(
                    HttpStatus.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST.value(),
                    new Date(),
                    Collections.singletonList(new ParamMessage("id", "UUID has to be represented by standard 36-char representation")),
                    request.getDescription(false)
            );
        }
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                Collections.singletonList(new ParamMessage("error", ex.getOriginalMessage())),
                request.getDescription(false)
        );
    }
}

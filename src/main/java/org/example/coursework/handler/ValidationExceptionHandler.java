/*
 * Copyright (c) 2021-2021.
 * Written by Maksim Stepanenko <stepanenko-qa@yandex.ru>
 */

package org.example.coursework.handler;

import java.util.Date;
import javax.validation.ConstraintViolationException;

import org.example.coursework.error.EngagedException;
import org.example.coursework.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler({EngagedException.class, ConstraintViolationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleBadRequest(RuntimeException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }
}

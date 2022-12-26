package org.hse.parkings.handler;

import org.hse.parkings.exception.ErrorMessage;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.exception.ParamMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.Date;

@RestControllerAdvice
public class NotFoundExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected ErrorMessage handleNotFound(NotFoundException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                Collections.singletonList(new ParamMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }
}

package org.hse.parkings.handler.exception;

import org.hse.parkings.exception.AlreadyExistsException;
import org.hse.parkings.model.error.CauseMessage;
import org.hse.parkings.model.error.Error;
import org.hse.parkings.utils.DateTimeProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

@RestControllerAdvice
public class AlreadyExistsExceptionHandler {

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    protected Error handleAlreadyExists(AlreadyExistsException ex, WebRequest request) {
        return new Error(
                HttpStatus.CONFLICT,
                HttpStatus.CONFLICT.value(),
                DateTimeProvider.getInstance().getZonedDateTime(),
                Collections.singletonList(new CauseMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }
}

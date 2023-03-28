package org.hse.parkings.handler.exception;

import org.hse.parkings.exception.AlreadyExistsException;
import org.hse.parkings.exception.ErrorMessage;
import org.hse.parkings.exception.ParamMessage;
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
    protected ErrorMessage handleAlreadyExists(AlreadyExistsException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.CONFLICT,
                HttpStatus.CONFLICT.value(),
                DateTimeProvider.getInstance().getZonedDateTime(),
                Collections.singletonList(new ParamMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }
}

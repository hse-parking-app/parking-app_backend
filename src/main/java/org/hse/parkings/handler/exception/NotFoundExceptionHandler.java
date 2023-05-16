package org.hse.parkings.handler.exception;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.exception.NotFoundException;
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
@RequiredArgsConstructor
public class NotFoundExceptionHandler {

    private final DateTimeProvider dateTimeProvider;

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected Error handleNotFound(NotFoundException ex, WebRequest request) {
        return new Error(
                HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.value(),
                dateTimeProvider.getZonedDateTime(),
                Collections.singletonList(new CauseMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }
}

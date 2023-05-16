package org.hse.parkings.handler.exception;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.error.CauseMessage;
import org.hse.parkings.model.error.Error;
import org.hse.parkings.utils.DateTimeProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

@RestControllerAdvice
@RequiredArgsConstructor
public class MediaTypeExceptionHandler {

    private final DateTimeProvider dateTimeProvider;

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    protected Error handleMediaTypeException(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        return new Error(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                dateTimeProvider.getZonedDateTime(),
                Collections.singletonList(new CauseMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }
}

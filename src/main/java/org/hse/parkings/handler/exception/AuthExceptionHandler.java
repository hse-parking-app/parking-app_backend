package org.hse.parkings.handler.exception;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.hse.parkings.exception.AuthException;
import org.hse.parkings.model.error.CauseMessage;
import org.hse.parkings.model.error.Error;
import org.hse.parkings.utils.DateTimeProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

@RestControllerAdvice
@RequiredArgsConstructor
public class AuthExceptionHandler {

    private final DateTimeProvider dateTimeProvider;

    @ExceptionHandler({AuthException.class, JwtException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    protected Error handleMediaTypeException(RuntimeException ex, WebRequest request) {
        return new Error(
                HttpStatus.UNAUTHORIZED,
                HttpStatus.UNAUTHORIZED.value(),
                dateTimeProvider.getZonedDateTime(),
                Collections.singletonList(new CauseMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    protected Error handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return new Error(
                HttpStatus.FORBIDDEN,
                HttpStatus.FORBIDDEN.value(),
                dateTimeProvider.getZonedDateTime(),
                Collections.singletonList(new CauseMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }
}

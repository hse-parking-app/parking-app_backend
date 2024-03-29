package org.hse.parkings.handler.exception;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.error.CauseMessage;
import org.hse.parkings.model.error.Error;
import org.hse.parkings.utils.DateTimeProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;
import java.util.Collections;

@RestControllerAdvice
@RequiredArgsConstructor
public class SQLExceptionHandler {

    private final DateTimeProvider dateTimeProvider;

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected Error handleSQLException(SQLException ex, WebRequest request) {
        return new Error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                dateTimeProvider.getZonedDateTime(),
                Collections.singletonList(new CauseMessage("error", ex.getMessage())),
                request.getDescription(false)
        );
    }
}

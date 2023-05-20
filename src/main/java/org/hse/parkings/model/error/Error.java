package org.hse.parkings.model.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class Error {

    private HttpStatus error;
    private int code;
    private ZonedDateTime timestamp;
    private List<CauseMessage> messages;
    private String path;
}

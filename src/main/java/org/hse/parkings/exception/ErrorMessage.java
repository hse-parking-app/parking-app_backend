package org.hse.parkings.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorMessage {

    private HttpStatus error;
    private int code;
    private ZonedDateTime timestamp;
    private List<ParamMessage> messages;
    private String path;
}

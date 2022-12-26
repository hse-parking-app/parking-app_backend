package org.hse.parkings.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorMessage {

    private HttpStatus error;
    private int code;
    private Date timestamp;
    private List<ParamMessage> messages;
    private String path;
}

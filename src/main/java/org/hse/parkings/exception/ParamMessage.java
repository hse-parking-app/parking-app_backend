package org.hse.parkings.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParamMessage {
    private String param;
    private String message;
}

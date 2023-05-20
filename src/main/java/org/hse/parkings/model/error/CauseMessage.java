package org.hse.parkings.model.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CauseMessage {

    private String cause;
    private String message;
}

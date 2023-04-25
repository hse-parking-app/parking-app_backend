package org.hse.parkings.model.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CauseMessage {

    private String cause;
    private String message;
}

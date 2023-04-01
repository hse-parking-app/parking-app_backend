package org.hse.parkings.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PBQElement {

    UUID id;

    Boolean isEndTime;

    LocalDateTime executionTime;

    Runnable runnable;
}

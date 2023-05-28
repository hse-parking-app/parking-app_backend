package org.hse.parkings.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PBQElement {

    UUID id;

    Boolean isEndTime;

    LocalDateTime executionTime;

    Runnable runnable;
}

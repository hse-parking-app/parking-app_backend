package org.hse.parkings.model;

import lombok.Builder;
import lombok.Data;
import org.hse.parkings.validate.DurationIsLess24Hours;
import org.hse.parkings.validate.NotWeekends;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NotWeekends
@DurationIsLess24Hours
public class Reservation {

    @Builder.Default
    UUID id = UUID.randomUUID();

    @NotNull
    UUID carId;

    @NotNull
    UUID employeeId;

    @NotNull
    UUID parkingSpotId;

    @FutureOrPresent
    LocalDateTime startTime;
    LocalDateTime endTime;
}

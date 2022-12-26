package org.hse.parkings.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.hse.parkings.validate.DurationIsLess24Hours;
import org.hse.parkings.validate.NotWeekends;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NotWeekends
@DurationIsLess24Hours
@Builder(toBuilder = true)
public class Reservation {
    @Builder.Default
    UUID id = UUID.randomUUID();
    @NonNull
    UUID carId;
    @NonNull
    UUID employeeId;
    @NonNull
    UUID parkingSpotId;

    @FutureOrPresent
    LocalDateTime startTime;
    LocalDateTime endTime;
}

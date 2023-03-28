package org.hse.parkings.model;

import lombok.Builder;
import lombok.Data;
import org.hse.parkings.validate.DurationIsLess24Hours;
import org.hse.parkings.validate.NotMultiDayReservation;
import org.hse.parkings.validate.NotWeekends;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@DurationIsLess24Hours(message = "Duration between start time and end time must be more than 0 and less than 24 hours")
@NotWeekends(message = "Cannot book a parking spot on Saturday and Sunday")
@NotMultiDayReservation(message = "Reservation cannot belong to more than one day")
public class Reservation {

    @Builder.Default
    UUID id = UUID.randomUUID();

    @NotNull(message = "Reservation must be connected to car via UUID")
    UUID carId;

    @NotNull(message = "Reservation must be connected to employee via UUID")
    UUID employeeId;

    @NotNull(message = "Reservation must be connected to parking spot via UUID")
    UUID parkingSpotId;

    @NotNull(message = "Reservation start time is required parameter")
    @FutureOrPresent(message = "Start time must be current or future time")
    LocalDateTime startTime;

    @NotNull(message = "Reservation end time is required parameter")
    LocalDateTime endTime;
}

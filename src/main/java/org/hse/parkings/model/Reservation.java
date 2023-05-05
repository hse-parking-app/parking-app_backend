package org.hse.parkings.model;

import lombok.Builder;
import lombok.Data;
import org.hse.parkings.validate.DurationIsLess24Hours;
import org.hse.parkings.validate.NotMultiDayReservation;
import org.hse.parkings.validate.NotWeekends;
import org.hse.parkings.validate.groups.reservation.AppUserReservation;
import org.hse.parkings.validate.groups.reservation.DefaultReservation;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@DurationIsLess24Hours(message = "Duration between start time and end time must be more than 0 and less than 24 hours", groups = {DefaultReservation.class, AppUserReservation.class})
@NotWeekends(message = "Cannot book a parking spot on Saturday and Sunday", groups = {DefaultReservation.class, AppUserReservation.class})
@NotMultiDayReservation(message = "Reservation cannot belong to more than one day", groups = {DefaultReservation.class, AppUserReservation.class})
public class Reservation {

    @Builder.Default
    UUID id = UUID.randomUUID();

    @NotNull(message = "Reservation must be connected to car via UUID", groups = {DefaultReservation.class, AppUserReservation.class})
    UUID carId;

    @NotNull(message = "Reservation must be connected to employee via UUID", groups = {DefaultReservation.class})
    UUID employeeId;

    @NotNull(message = "Reservation must be connected to parking spot via UUID", groups = {DefaultReservation.class, AppUserReservation.class})
    UUID parkingSpotId;

    @NotNull(message = "Reservation start time is required parameter", groups = {DefaultReservation.class, AppUserReservation.class})
    @FutureOrPresent(message = "Start time must be current or future time", groups = {DefaultReservation.class, AppUserReservation.class})
    LocalDateTime startTime;

    @NotNull(message = "Reservation end time is required parameter", groups = {DefaultReservation.class, AppUserReservation.class})
    LocalDateTime endTime;
}

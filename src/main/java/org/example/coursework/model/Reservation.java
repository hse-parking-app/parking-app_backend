package org.example.coursework.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.coursework.validate.DurationIsLess24Hours;
import org.example.coursework.validate.NotWeekends;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NotWeekends
@DurationIsLess24Hours
@AllArgsConstructor
public class Reservation {
    UUID id;
    UUID carId;
    UUID employeeId;
    UUID parkingSpotId;

    @FutureOrPresent
    LocalDateTime startTime;
    LocalDateTime endTime;

    public Reservation(
            UUID carId,
            UUID employeeId,
            UUID parkingSpotId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        this.id = UUID.randomUUID();
        this.carId = carId;
        this.employeeId = employeeId;
        this.parkingSpotId = parkingSpotId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Reservation() {
        this.id = UUID.randomUUID();
    }
}

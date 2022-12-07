package org.example.coursework.model;

import java.util.UUID;
import javax.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParkingSpot {
    UUID id;

    @PositiveOrZero
    Integer parkingNumber;
    Boolean isFree;

    public ParkingSpot(Integer parkingNumber, Boolean isFree) {
        this.id = UUID.randomUUID();
        this.parkingNumber = parkingNumber;
        this.isFree = isFree;
    }

    public ParkingSpot() {
        this.id = UUID.randomUUID();
    }
}

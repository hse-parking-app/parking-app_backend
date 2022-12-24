package org.example.coursework.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.PositiveOrZero;
import java.util.UUID;

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

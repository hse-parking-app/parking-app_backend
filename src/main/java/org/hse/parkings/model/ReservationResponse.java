package org.hse.parkings.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class ReservationResponse {

    String carModel;

    String carRegistryNumber;

    String employeeName;

    String parkingSpotNumber;

    LocalDateTime startTime;

    LocalDateTime endTime;
}

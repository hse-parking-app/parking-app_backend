package org.hse.parkings.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class ParkingSpot {
    @Builder.Default
    UUID id = UUID.randomUUID();
    @NonNull @NotBlank
    String parkingNumber;
    @NonNull
    Boolean isFree;
}

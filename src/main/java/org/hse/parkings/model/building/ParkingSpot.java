package org.hse.parkings.model.building;

import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class ParkingSpot {

    @Builder.Default
    UUID id = UUID.randomUUID();

    @NotNull(message = "Parking spot must be connected to parking level via UUID")
    UUID levelId;

    @NotNull(message = "Parking spot must be connected to building via UUID")
    UUID buildingId;

    @NotNull(message = "Parking spot parking number is required parameter")
    @NotBlank(message = "Parking number cannot be blank")
    String parkingNumber;

    @NotNull(message = "isFree is required parameter")
    Boolean isFree;

    @NotNull(message = "Parking spot canvas size is required parameter")
    @Valid
    CanvasSize canvas;

    @NotNull(message = "onCanvasCoords is required parameter")
    @Valid
    OnCanvasCoords onCanvasCoords;
}

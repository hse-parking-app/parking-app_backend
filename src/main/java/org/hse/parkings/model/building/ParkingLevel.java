package org.hse.parkings.model.building;

import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class ParkingLevel {

    @Builder.Default
    UUID id = UUID.randomUUID();

    @NotNull(message = "Parking level must be connected to building via UUID")
    UUID buildingId;

    @NotNull(message = "Parking level layer name is required parameter")
    @NotBlank(message = "Layer name cannot be blank")
    String layerName;

    @NotNull(message = "Parking level number of spots is required parameter")
    @Positive(message = "Number of spots must be positive")
    int numberOfSpots;

    @NotNull(message = "Parking level canvas size is required parameter")
    @Valid
    CanvasSize canvas;
}

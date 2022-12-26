package org.hse.parkings.model.building;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class CanvasSize {

    @NotNull @Positive(message = "Width must be positive")
    int width;

    @NotNull @Positive(message = "Height must be positive")
    int height;
}

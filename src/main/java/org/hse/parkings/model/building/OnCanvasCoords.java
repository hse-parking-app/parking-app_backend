package org.hse.parkings.model.building;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
public class OnCanvasCoords {

    @PositiveOrZero(message = "x coord must be zero or positive")
    int x;

    @PositiveOrZero(message = "y coord must be zero or positive")
    int y;
}

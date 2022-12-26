package org.hse.parkings.model.building;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class Building {

    @Builder.Default
    UUID id = UUID.randomUUID();

    @NotNull(message = "Building name is required parameter")
    @NotBlank(message = "Name cannot be blank")
    String name;

    @NotNull(message = "Building address is required parameter")
    @NotBlank(message = "Address cannot be blank")
    String address;

    @NotNull(message = "Building number of levels is required parameter")
    @Positive(message = "Number of levels cannot be negative or zero")
    int numberOfLevels;
}

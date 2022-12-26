package org.hse.parkings.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class Car {
    UUID id;

    @NotNull(message = "Car model is required parameter")
    @NotBlank(message = "Model cannot be blank")
    String model;

    @Positive(message = "Length must be a positive number")
    Double lengthMeters;

    @Positive(message = "Weight must be a positive number")
    Double weightTons;

    @NotNull(message = "Car registry number is required parameter")
    @NotBlank(message = "Registry number cannot be blank")
    String registryNumber;
}

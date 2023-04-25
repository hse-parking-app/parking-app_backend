package org.hse.parkings.model;

import lombok.Builder;
import lombok.Data;
import org.hse.parkings.validate.groups.AppUserCar;
import org.hse.parkings.validate.groups.DefaultCar;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class Car {

    @Builder.Default
    UUID id = UUID.randomUUID();

    @NotNull(message = "Car must be connected to owner employee via UUID", groups = {DefaultCar.class})
    UUID ownerId;

    @NotNull(message = "Car model is required parameter", groups = {DefaultCar.class, AppUserCar.class})
    @NotBlank(message = "Model cannot be blank", groups = {DefaultCar.class, AppUserCar.class})
    String model;

    @Positive(message = "Length must be a positive number", groups = {DefaultCar.class, AppUserCar.class})
    Double lengthMeters;

    @Positive(message = "Weight must be a positive number", groups = {DefaultCar.class, AppUserCar.class})
    Double weightTons;

    @NotNull(message = "Car registry number is required parameter", groups = {DefaultCar.class, AppUserCar.class})
    @NotBlank(message = "Registry number cannot be blank", groups = {DefaultCar.class, AppUserCar.class})
    String registryNumber;
}

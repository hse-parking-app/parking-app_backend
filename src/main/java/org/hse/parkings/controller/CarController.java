package org.hse.parkings.controller;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.Car;
import org.hse.parkings.service.AuthService;
import org.hse.parkings.service.CarService;
import org.hse.parkings.validate.groups.car.AppUserCar;
import org.hse.parkings.validate.groups.car.DefaultCar;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    private final AuthService authService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Set<Car> getAll() {
        return carService.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Car create(@Validated(DefaultCar.class) @RequestBody Car car) {
        return carService.save(car);
    }

    @GetMapping("/{carId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Car get(@PathVariable UUID carId) {
        return carService.findCar(carId);
    }

    @DeleteMapping("/{carId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void delete(@PathVariable UUID carId) {
        carService.delete(carId);
    }

    @PutMapping(value = "/{carId}", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Car edit(@PathVariable UUID carId, @Valid @RequestBody Car car) {
        car.setId(carId);
        return carService.update(car);
    }

    @GetMapping("/employee")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Set<Car> getEmployeeCars() {
        return carService.findEmployeesCars(authService.getAuthInfo().getId());
    }

    @PostMapping(value = "/employee", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Car createEmployeeCar(@Validated(AppUserCar.class) @RequestBody Car car) {
        return carService.saveEmployeeCar(car);
    }

    @PutMapping(value = "/{carId}/employee", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Car editEmployeeCar(@PathVariable UUID carId, @Validated(AppUserCar.class) @RequestBody Car car) {
        car.setId(carId);
        return carService.updateEmployeeCar(car);
    }

    @DeleteMapping(value = "/{carId}/employee")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    void deleteEmployeeCar(@PathVariable UUID carId) {
        carService.deleteEmployeeCar(carId);
    }
}

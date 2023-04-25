package org.hse.parkings.controller;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.Car;
import org.hse.parkings.model.jwt.JwtAuthentication;
import org.hse.parkings.service.AuthService;
import org.hse.parkings.service.CarService;
import org.hse.parkings.validate.groups.AppUserCar;
import org.hse.parkings.validate.groups.DefaultCar;
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

    @PostMapping(value = "/employee", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Car createEmployeeCar(@Validated(AppUserCar.class) @RequestBody Car car) {
        JwtAuthentication authInfo = authService.getAuthInfo();
        car.setOwnerId(authInfo.getId());
        return carService.save(car);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void deleteAll() {
        carService.deleteAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Car get(@PathVariable UUID id) {
        return carService.findCar(id);
    }

    @GetMapping("/employee")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Set<Car> getEmployeeCars() {
        JwtAuthentication authInfo = authService.getAuthInfo();
        return carService.findEmployeesCars(authInfo.getId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void delete(@PathVariable UUID id) {
        carService.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Car edit(@PathVariable UUID id, @Valid @RequestBody Car car) {
        car.setId(id);
        return carService.update(car);
    }
}

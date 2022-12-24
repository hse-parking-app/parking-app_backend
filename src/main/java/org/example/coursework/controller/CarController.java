package org.example.coursework.controller;

import org.example.coursework.error.NotFoundException;
import org.example.coursework.model.Car;
import org.example.coursework.service.CarService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    Set<Car> getAll() throws NotFoundException {
        return carService.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    Car create(@RequestBody Car car) {
        return carService.save(car);
    }

    @DeleteMapping
    @Secured("ROLE_ADMIN")
    void deleteAll() {
        carService.deleteAll();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    Car get(@PathVariable UUID id) {
        return carService.findCar(id);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    Car edit(@PathVariable UUID id, @RequestBody Car car) {
        car.setId(id);
        return carService.update(car);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    void delete(@PathVariable UUID id) {
        carService.delete(id);
    }
}

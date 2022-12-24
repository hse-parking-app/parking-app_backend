package org.example.coursework.controller;

import org.example.coursework.error.NotFoundException;
import org.example.coursework.model.ParkingSpot;
import org.example.coursework.service.ParkingSpotService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/parkingSpots")
public class ParkingSpotController {
    private final ParkingSpotService service;

    public ParkingSpotController(ParkingSpotService service) {
        this.service = service;
    }

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    Set<ParkingSpot> getAll() throws NotFoundException {
        return service.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    ParkingSpot create(@Valid @RequestBody ParkingSpot parkingSpot) {
        return service.save(parkingSpot);
    }

    @DeleteMapping
    @Secured("ROLE_ADMIN")
    void deleteAll() {
        service.deleteAll();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    ParkingSpot get(@PathVariable UUID id) {
        return service.find(id);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    ParkingSpot edit(@PathVariable UUID id, @Valid @RequestBody ParkingSpot parkingSpot) {
        parkingSpot.setId(id);
        return service.update(parkingSpot);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}

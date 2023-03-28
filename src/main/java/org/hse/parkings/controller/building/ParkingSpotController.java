package org.hse.parkings.controller.building;

import org.hse.parkings.model.building.ParkingSpot;
import org.hse.parkings.service.building.ParkingSpotService;
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
    Set<ParkingSpot> getAll() {
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
        return service.findParkingSpot(id);
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

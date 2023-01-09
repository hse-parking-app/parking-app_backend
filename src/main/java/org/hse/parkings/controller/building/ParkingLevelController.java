package org.hse.parkings.controller.building;

import org.hse.parkings.model.building.ParkingLevel;
import org.hse.parkings.model.building.ParkingSpot;
import org.hse.parkings.service.building.ParkingLevelService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/parkingLevels")
public class ParkingLevelController {

    private final ParkingLevelService service;

    public ParkingLevelController(ParkingLevelService service) {
        this.service = service;
    }

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    Set<ParkingLevel> getAll() {
        return service.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    ParkingLevel create(@Valid @RequestBody ParkingLevel parkingLevel) {
        return service.save(parkingLevel);
    }

    @DeleteMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    void deleteAll() {
        service.deleteAll();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    ParkingLevel get(@PathVariable UUID id) {
        return service.findParkingLevel(id);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    ParkingLevel edit(@PathVariable UUID id, @Valid @RequestBody ParkingLevel parkingLevel) {
        parkingLevel.setId(id);
        return service.update(parkingLevel);
    }

    @GetMapping("/{levelId}/spots")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    Set<ParkingSpot> findParkingSpots(@PathVariable UUID levelId) {
        return service.findParkingSpots(levelId);
    }
}

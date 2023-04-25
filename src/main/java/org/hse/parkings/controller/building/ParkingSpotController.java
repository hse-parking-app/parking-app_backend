package org.hse.parkings.controller.building;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.building.ParkingSpot;
import org.hse.parkings.service.building.ParkingSpotService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/parkingSpots")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Set<ParkingSpot> getAll() {
        return service.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ParkingSpot create(@Valid @RequestBody ParkingSpot parkingSpot) {
        return service.save(parkingSpot);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void deleteAll() {
        service.deleteAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    ParkingSpot get(@PathVariable UUID id) {
        return service.findParkingSpot(id);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ParkingSpot edit(@PathVariable UUID id, @Valid @RequestBody ParkingSpot parkingSpot) {
        parkingSpot.setId(id);
        return service.update(parkingSpot);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}

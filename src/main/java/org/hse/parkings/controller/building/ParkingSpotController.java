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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
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

    @GetMapping("/{spotId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    ParkingSpot get(@PathVariable UUID spotId) {
        return service.findParkingSpot(spotId);
    }

    @PutMapping(value = "/{spotId}", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ParkingSpot edit(@PathVariable UUID spotId, @Valid @RequestBody ParkingSpot parkingSpot) {
        parkingSpot.setId(spotId);
        return service.update(parkingSpot);
    }

    @DeleteMapping("/{spotId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void delete(@PathVariable UUID spotId) {
        service.delete(spotId);
    }
}

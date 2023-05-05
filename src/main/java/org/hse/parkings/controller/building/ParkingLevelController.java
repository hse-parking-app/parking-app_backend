package org.hse.parkings.controller.building;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.building.ParkingLevel;
import org.hse.parkings.model.building.ParkingSpot;
import org.hse.parkings.service.building.ParkingLevelService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/parkingLevels")
@RequiredArgsConstructor
public class ParkingLevelController {

    private final ParkingLevelService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Set<ParkingLevel> getAll() {
        return service.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ParkingLevel create(@Valid @RequestBody ParkingLevel parkingLevel) {
        return service.save(parkingLevel);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void deleteAll() {
        service.deleteAll();
    }

    @GetMapping("/{levelId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    ParkingLevel get(@PathVariable UUID levelId) {
        return service.findParkingLevel(levelId);
    }

    @DeleteMapping("/{levelId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void delete(@PathVariable UUID levelId) {
        service.delete(levelId);
    }

    @PutMapping(value = "/{levelId}", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ParkingLevel edit(@PathVariable UUID levelId, @Valid @RequestBody ParkingLevel parkingLevel) {
        parkingLevel.setId(levelId);
        return service.update(parkingLevel);
    }

    @GetMapping("/{levelId}/spots")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Set<ParkingSpot> findParkingSpots(@PathVariable UUID levelId) {
        return service.findParkingSpots(levelId);
    }

    @GetMapping("/{levelId}/freeSpotsInInterval")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Set<ParkingSpot> getFreeSpotsOnLevelInInterval(@PathVariable UUID levelId,
                                                   @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                                   @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return service.getFreeSpotsOnLevelInInterval(levelId, startTime, endTime);
    }
}

package org.hse.parkings.controller.building;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.building.Building;
import org.hse.parkings.model.building.ParkingLevel;
import org.hse.parkings.service.building.BuildingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/building")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Set<Building> getAll() {
        return service.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Building create(@Valid @RequestBody Building building) {
        return service.save(building);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void deleteAll() {
        service.deleteAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Building get(@PathVariable UUID id) {
        return service.findBuilding(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Building edit(@PathVariable UUID id, @Valid @RequestBody Building building) {
        building.setId(id);
        return service.update(building);
    }

    @GetMapping("/{buildingId}/levels")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Set<ParkingLevel> findBuildingLevels(@PathVariable UUID buildingId) {
        return service.findBuildingLevels(buildingId);
    }
}

package org.hse.parkings.controller.building;

import org.hse.parkings.model.building.Building;
import org.hse.parkings.model.building.ParkingLevel;
import org.hse.parkings.service.building.BuildingService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/building")
public class BuildingController {

    private final BuildingService service;

    public BuildingController(BuildingService service) {
        this.service = service;
    }

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    Set<Building> getAll() {
        return service.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    Building create(@Valid @RequestBody Building building) {
        return service.save(building);
    }

    @DeleteMapping
    @Secured("ROLE_ADMIN")
    void deleteAll() {
        service.deleteAll();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    Building get(@PathVariable UUID id) {
        return service.findBuilding(id);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    Building edit(@PathVariable UUID id, @Valid @RequestBody Building building) {
        building.setId(id);
        return service.update(building);
    }

    @GetMapping("/{buildingId}/levels")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    List<ParkingLevel> findBuildingLevels(@PathVariable UUID buildingId) {
        return service.findBuildingLevels(buildingId);
    }
}

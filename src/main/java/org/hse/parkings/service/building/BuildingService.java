package org.hse.parkings.service.building;

import org.hse.parkings.dao.building.BuildingRepository;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.building.Building;
import org.hse.parkings.model.building.ParkingLevel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.buildingCache;

@Service
public class BuildingService {

    private final BuildingRepository repository;

    public BuildingService(BuildingRepository repository) {
        this.repository = repository;
    }

    public Building save(Building building) {
        Building toSave = Building.builder()
                .name(building.getName())
                .address(building.getAddress())
                .numberOfLevels(building.getNumberOfLevels()).build();
        repository.save(toSave);
        buildingCache.remove(toSave.getId());
        return findBuilding(toSave.getId());
    }

    public Building update(Building building) {
        repository.update(building);
        buildingCache.remove(building.getId());
        return findBuilding(building.getId());
    }

    public void delete(UUID id) {
        repository.delete(id);
        buildingCache.remove(id);
    }

    public void deleteAll() {
        repository.deleteAll();
        buildingCache.clear();
    }

    public Building findBuilding(UUID id) throws NotFoundException {
        if (buildingCache.containsKey(id)) {
            return buildingCache.get(id);
        }
        Building building = repository
                .find(id)
                .orElseThrow(() -> new NotFoundException("Building with id = " + id + " not found"));
        buildingCache.put(id, building);
        return building;
    }

    public Set<Building> findAll() {
        return repository.findAll();
    }

    public List<ParkingLevel> findBuildingLevels(UUID buildingId) {
        return repository.findBuildingLevels(buildingId);
    }
}

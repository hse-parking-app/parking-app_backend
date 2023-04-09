package org.hse.parkings.service.building;

import org.hse.parkings.dao.building.BuildingRepository;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.building.Building;
import org.hse.parkings.model.building.ParkingLevel;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.*;

@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public Building save(Building building) {
        Building toSave = Building.builder()
                .name(building.getName())
                .address(building.getAddress())
                .numberOfLevels(building.getNumberOfLevels()).build();
        buildingRepository.save(toSave);
        buildingCache.remove(toSave.getId());
        return findBuilding(toSave.getId());
    }

    public Building update(Building building) {
        buildingRepository.update(building);
        buildingCache.remove(building.getId());
        return findBuilding(building.getId());
    }

    public void delete(UUID id) {
        Set<ParkingLevel> buildingLevels = buildingRepository.findBuildingLevels(id);
        buildingLevels.forEach(item -> {
            parkingLevelCache.remove(item.getId());
            parkingLevelSpotsCache.remove(item.getId());
        });

        buildingRepository.delete(id);
        buildingCache.remove(id);
        buildingLevelsCache.remove(id);
        parkingSpotCache.clear();
    }

    public void deleteAll() {
        buildingRepository.deleteAll();
        buildingCache.clear();
        parkingLevelCache.clear();
        buildingLevelsCache.clear();
        parkingSpotCache.clear();
        parkingLevelSpotsCache.clear();
    }

    public Building findBuilding(UUID id) throws NotFoundException {
        if (buildingCache.containsKey(id)) {
            return buildingCache.get(id);
        }
        Building building = buildingRepository.find(id)
                .orElseThrow(() -> new NotFoundException("Building with id = " + id + " not found"));
        buildingCache.put(id, building);
        return building;
    }

    public Set<Building> findAll() {
        return buildingRepository.findAll();
    }

    public Set<ParkingLevel> findBuildingLevels(UUID buildingId) throws NotFoundException {
        if (buildingLevelsCache.containsKey(buildingId)) {
            return buildingLevelsCache.get(buildingId);
        }
        Building building = buildingRepository.find(buildingId)
                .orElseThrow(() -> new NotFoundException("Building with id = " + buildingId + " not found"));
        buildingCache.put(buildingId, building);
        Set<ParkingLevel> buildingLevels = buildingRepository.findBuildingLevels(buildingId);
        buildingLevelsCache.put(buildingId, buildingLevels);

        return buildingLevels;
    }
}

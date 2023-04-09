package org.hse.parkings.service.building;

import org.hse.parkings.dao.building.BuildingRepository;
import org.hse.parkings.dao.building.ParkingLevelRepository;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.building.ParkingLevel;
import org.hse.parkings.model.building.ParkingSpot;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.*;

@Service
public class ParkingLevelService {

    private final ParkingLevelRepository parkingLevelRepository;

    private final BuildingRepository buildingRepository;

    public ParkingLevelService(ParkingLevelRepository parkingLevelRepository, BuildingRepository buildingRepository) {
        this.parkingLevelRepository = parkingLevelRepository;
        this.buildingRepository = buildingRepository;
    }

    public ParkingLevel save(ParkingLevel parkingLevel) throws NotFoundException {
        ParkingLevel toSave = ParkingLevel.builder()
                .buildingId(parkingLevel.getBuildingId())
                .layerName(parkingLevel.getLayerName())
                .numberOfSpots(parkingLevel.getNumberOfSpots())
                .canvas(parkingLevel.getCanvas()).build();
        buildingRepository.find(toSave.getBuildingId())
                .orElseThrow(() -> new NotFoundException("Building with id = " + toSave.getBuildingId() + " not found"));
        parkingLevelRepository.save(toSave);
        parkingLevelCache.remove(toSave.getId());
        buildingLevelsCache.remove(toSave.getBuildingId());
        return findParkingLevel(toSave.getId());
    }

    public ParkingLevel update(ParkingLevel parkingLevel) throws NotFoundException {
        buildingRepository.find(parkingLevel.getBuildingId())
                .orElseThrow(() -> new NotFoundException("Building with id = " + parkingLevel.getBuildingId() + " not found"));
        parkingLevelRepository.update(parkingLevel);
        parkingLevelCache.remove(parkingLevel.getId());
        buildingLevelsCache.remove(parkingLevel.getBuildingId());
        return findParkingLevel(parkingLevel.getId());
    }

    public void delete(UUID id) throws NotFoundException {
        ParkingLevel parkingLevel = parkingLevelRepository.find(id)
                .orElseThrow(() -> new NotFoundException("ParkingLevel with id = " + id + " not found"));

        parkingLevelRepository.delete(id);
        parkingLevelCache.remove(id);
        buildingLevelsCache.remove(parkingLevel.getBuildingId());
    }

    public void deleteAll() {
        parkingLevelRepository.deleteAll();
        parkingLevelCache.clear();
        buildingLevelsCache.clear();
        parkingSpotCache.clear();
        parkingLevelSpotsCache.clear();
    }

    public ParkingLevel findParkingLevel(UUID id) throws NotFoundException {
        if (parkingLevelCache.containsKey(id)) {
            return parkingLevelCache.get(id);
        }
        ParkingLevel parkingLevel = parkingLevelRepository.find(id)
                .orElseThrow(() -> new NotFoundException("ParkingLevel with id = " + id + " not found"));
        parkingLevelCache.put(id, parkingLevel);

        return parkingLevel;
    }

    public Set<ParkingLevel> findAll() {
        return parkingLevelRepository.findAll();
    }

    public Set<ParkingSpot> findParkingSpots(UUID levelId) {
        if (parkingLevelSpotsCache.containsKey(levelId)) {
            return parkingLevelSpotsCache.get(levelId);
        }
        ParkingLevel parkingLevel = parkingLevelRepository.find(levelId)
                .orElseThrow(() -> new NotFoundException("ParkingLevel with id = " + levelId + " not found"));
        parkingLevelCache.put(levelId, parkingLevel);
        Set<ParkingSpot> parkingLevelSpots = parkingLevelRepository.findParkingSpots(levelId);
        parkingLevelSpotsCache.put(levelId, parkingLevelSpots);

        return parkingLevelSpots;
    }
}

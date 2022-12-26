package org.hse.parkings.service.building;

import org.hse.parkings.dao.building.ParkingLevelRepository;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.building.ParkingLevel;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.parkingLevelCache;

@Service
public class ParkingLevelService {

    private final ParkingLevelRepository repository;

    public ParkingLevelService(ParkingLevelRepository repository) {
        this.repository = repository;
    }

    public ParkingLevel save(ParkingLevel parkingLevel) {
        ParkingLevel toSave = ParkingLevel.builder()
                .buildingId(parkingLevel.getBuildingId())
                .layerName(parkingLevel.getLayerName())
                .numberOfSpots(parkingLevel.getNumberOfSpots())
                .canvas(parkingLevel.getCanvas()).build();
        repository.save(toSave);
        parkingLevelCache.remove(toSave.getId());
        return findParkingLevel(toSave.getId());
    }

    public ParkingLevel update(ParkingLevel parkingLevel) {
        repository.update(parkingLevel);
        parkingLevelCache.remove(parkingLevel.getId());
        return findParkingLevel(parkingLevel.getId());
    }

    public void delete(UUID id) {
        repository.delete(id);
        parkingLevelCache.remove(id);
    }

    public void deleteAll() {
        repository.deleteAll();
        parkingLevelCache.clear();
    }

    public ParkingLevel findParkingLevel(UUID id) throws NotFoundException {
        if (parkingLevelCache.containsKey(id)) {
            return parkingLevelCache.get(id);
        }
        ParkingLevel parkingLevel = repository
                .find(id)
                .orElseThrow(() -> new NotFoundException("ParkingLevel with id = " + id + " not found"));
        parkingLevelCache.put(id, parkingLevel);
        return parkingLevel;
    }

    public Set<ParkingLevel> findAll() {
        return repository.findAll();
    }
}

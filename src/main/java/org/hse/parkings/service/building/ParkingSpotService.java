package org.hse.parkings.service.building;

import org.hse.parkings.dao.building.BuildingRepository;
import org.hse.parkings.dao.building.ParkingLevelRepository;
import org.hse.parkings.dao.building.ParkingSpotRepository;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.building.ParkingSpot;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.parkingSpotCache;

@Service
public class ParkingSpotService {

    private final ParkingSpotRepository repository;

    private final ParkingLevelRepository parkingLevelRepository;

    private final BuildingRepository buildingRepository;

    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository,
                              ParkingLevelRepository parkingLevelRepository,
                              BuildingRepository buildingRepository) {
        this.repository = parkingSpotRepository;
        this.parkingLevelRepository = parkingLevelRepository;
        this.buildingRepository = buildingRepository;
    }

    public ParkingSpot save(ParkingSpot parkingSpot) throws NotFoundException {
        ParkingSpot toSave = ParkingSpot.builder()
                .levelId(parkingSpot.getLevelId())
                .buildingId(parkingSpot.getBuildingId())
                .parkingNumber(parkingSpot.getParkingNumber())
                .isFree(parkingSpot.getIsFree())
                .canvas(parkingSpot.getCanvas())
                .onCanvasCoords(parkingSpot.getOnCanvasCoords()).build();
        parkingLevelRepository.find(toSave.getLevelId())
                .orElseThrow(() -> new NotFoundException("ParkingLevel with id = " + toSave.getLevelId() + " not found"));
        buildingRepository.find(toSave.getBuildingId())
                .orElseThrow(() -> new NotFoundException("Building with id = " + toSave.getBuildingId() + " not found"));
        repository.save(toSave);
        parkingSpotCache.remove(toSave.getId());
        return findParkingSpot(toSave.getId());
    }

    public ParkingSpot update(ParkingSpot parkingSpot) throws NotFoundException {
        parkingLevelRepository.find(parkingSpot.getLevelId())
                .orElseThrow(() -> new NotFoundException("ParkingLevel with id = " + parkingSpot.getLevelId() + " not found"));
        buildingRepository.find(parkingSpot.getBuildingId())
                .orElseThrow(() -> new NotFoundException("Building with id = " + parkingSpot.getBuildingId() + " not found"));
        repository.update(parkingSpot);
        parkingSpotCache.remove(parkingSpot.getId());
        return findParkingSpot(parkingSpot.getId());
    }

    public void delete(UUID id) {
        repository.delete(id);
        parkingSpotCache.remove(id);
    }

    public void deleteAll() {
        repository.deleteAll();
        parkingSpotCache.clear();
    }

    public ParkingSpot findParkingSpot(UUID id) throws NotFoundException {
        if (parkingSpotCache.containsKey(id)) {
            return parkingSpotCache.get(id);
        }
        ParkingSpot parkingSpot = repository
                .find(id)
                .orElseThrow(() -> new NotFoundException("ParkingSpot with id = " + id + " not found"));
        parkingSpotCache.put(id, parkingSpot);
        return parkingSpot;
    }

    public Set<ParkingSpot> findAll() {
        return repository.findAll();
    }
}

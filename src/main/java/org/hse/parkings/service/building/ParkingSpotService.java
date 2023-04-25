package org.hse.parkings.service.building;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.dao.building.BuildingRepository;
import org.hse.parkings.dao.building.ParkingLevelRepository;
import org.hse.parkings.dao.building.ParkingSpotRepository;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.building.ParkingSpot;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.parkingLevelSpotsCache;
import static org.hse.parkings.utils.Cache.parkingSpotCache;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;

    private final ParkingLevelRepository parkingLevelRepository;

    private final BuildingRepository buildingRepository;

    public ParkingSpot save(ParkingSpot parkingSpot) throws NotFoundException {
        ParkingSpot toSave = ParkingSpot.builder()
                .levelId(parkingSpot.getLevelId())
                .buildingId(parkingSpot.getBuildingId())
                .parkingNumber(parkingSpot.getParkingNumber())
                .isAvailable(parkingSpot.getIsAvailable())
                .isFree(parkingSpot.getIsFree())
                .canvas(parkingSpot.getCanvas())
                .onCanvasCoords(parkingSpot.getOnCanvasCoords()).build();
        parkingLevelRepository.find(toSave.getLevelId())
                .orElseThrow(() -> new NotFoundException("ParkingLevel with id = " + toSave.getLevelId() + " not found"));
        buildingRepository.find(toSave.getBuildingId())
                .orElseThrow(() -> new NotFoundException("Building with id = " + toSave.getBuildingId() + " not found"));
        parkingSpotRepository.save(toSave);
        parkingSpotCache.remove(toSave.getId());
        parkingLevelSpotsCache.remove(toSave.getLevelId());
        return findParkingSpot(toSave.getId());
    }

    public ParkingSpot update(ParkingSpot parkingSpot) throws NotFoundException {
        parkingLevelRepository.find(parkingSpot.getLevelId())
                .orElseThrow(() -> new NotFoundException("ParkingLevel with id = " + parkingSpot.getLevelId() + " not found"));
        buildingRepository.find(parkingSpot.getBuildingId())
                .orElseThrow(() -> new NotFoundException("Building with id = " + parkingSpot.getBuildingId() + " not found"));
        parkingSpotRepository.update(parkingSpot);
        parkingSpotCache.remove(parkingSpot.getId());
        parkingLevelSpotsCache.remove(parkingSpot.getLevelId());
        return findParkingSpot(parkingSpot.getId());
    }

    public void delete(UUID id) throws NotFoundException {
        ParkingSpot parkingSpot = parkingSpotRepository.find(id)
                .orElseThrow(() -> new NotFoundException("ParkingSpot with id = " + id + " not found"));

        parkingSpotRepository.delete(id);
        parkingSpotCache.remove(id);
        parkingLevelSpotsCache.remove(parkingSpot.getLevelId());
    }

    public void deleteAll() {
        parkingSpotRepository.deleteAll();
        parkingSpotCache.clear();
        parkingLevelSpotsCache.clear();
    }

    public ParkingSpot findParkingSpot(UUID id) throws NotFoundException {
        if (parkingSpotCache.containsKey(id)) {
            return parkingSpotCache.get(id);
        }
        ParkingSpot parkingSpot = parkingSpotRepository.find(id)
                .orElseThrow(() -> new NotFoundException("ParkingSpot with id = " + id + " not found"));
        parkingSpotCache.put(id, parkingSpot);
        return parkingSpot;
    }

    public Set<ParkingSpot> findAll() {
        return parkingSpotRepository.findAll();
    }
}

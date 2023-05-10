package org.hse.parkings.service.building;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.dao.building.ParkingSpotRepository;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.building.ParkingSpot;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.parkingSpotCache;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;

    private final ParkingLevelService parkingLevelService;

    private final BuildingService buildingRepository;

    public ParkingSpot save(ParkingSpot parkingSpot) {
        ParkingSpot toSave = ParkingSpot.builder()
                .levelId(parkingSpot.getLevelId())
                .buildingId(parkingSpot.getBuildingId())
                .parkingNumber(parkingSpot.getParkingNumber())
                .isAvailable(parkingSpot.getIsAvailable())
                .isFree(parkingSpot.getIsFree())
                .canvas(parkingSpot.getCanvas())
                .onCanvasCoords(parkingSpot.getOnCanvasCoords()).build();
        parkingLevelService.findParkingLevel(toSave.getLevelId());
        buildingRepository.findBuilding(toSave.getBuildingId());
        parkingSpotRepository.save(toSave);
        parkingSpotCache.remove(toSave.getId());
        return findParkingSpot(toSave.getId());
    }

    public ParkingSpot update(ParkingSpot parkingSpot) throws NotFoundException {
        parkingLevelService.findParkingLevel(parkingSpot.getLevelId());
        buildingRepository.findBuilding(parkingSpot.getBuildingId());
        parkingSpotRepository.update(parkingSpot);
        parkingSpotCache.remove(parkingSpot.getId());
        return findParkingSpot(parkingSpot.getId());
    }

    public void delete(UUID id) throws NotFoundException {
        parkingSpotRepository.find(id)
                .orElseThrow(() -> new NotFoundException("ParkingSpot with id = " + id + " not found"));

        parkingSpotRepository.delete(id);
        parkingSpotCache.remove(id);
    }

    public void deleteAll() {
        parkingSpotRepository.deleteAll();
        parkingSpotCache.clear();
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

    public void freeAllParkingSpots() {
        parkingSpotRepository.freeAllParkingSpots();
    }

    public void occupySpot(UUID id) {
        parkingSpotRepository.occupySpot(id);
    }

    public void freeSpot(UUID id) {
        parkingSpotRepository.freeSpot(id);
    }
}

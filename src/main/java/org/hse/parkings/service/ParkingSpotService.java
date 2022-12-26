package org.hse.parkings.service;

import org.hse.parkings.dao.ParkingSpotRepository;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.ParkingSpot;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.parkingSpotCache;

@Service
public class ParkingSpotService {
    private final ParkingSpotRepository repository;

    public ParkingSpotService(ParkingSpotRepository repository) {
        this.repository = repository;
    }

    public ParkingSpot save(ParkingSpot parkingSpot) {
        ParkingSpot toSave = ParkingSpot.builder()
                .parkingNumber(parkingSpot.getParkingNumber())
                .isFree(parkingSpot.getIsFree()).build();
        repository.save(toSave);
        parkingSpotCache.remove(toSave.getId());
        return find(toSave.getId());
    }

    public ParkingSpot update(ParkingSpot parkingSpot) {
        repository.update(parkingSpot);
        parkingSpotCache.remove(parkingSpot.getId());
        return find(parkingSpot.getId());
    }

    public void delete(UUID id) {
        repository.delete(id);
        parkingSpotCache.remove(id);
    }

    public void deleteAll() {
        repository.deleteAll();
        parkingSpotCache.clear();
    }

    public ParkingSpot find(UUID id) throws NotFoundException {
        if (parkingSpotCache.containsKey(id)) {
            return parkingSpotCache.get(id);
        } else {
            ParkingSpot parkingSpot = repository
                    .find(id)
                    .orElseThrow(() -> new NotFoundException("ParkingSpot with id = " + id + " not found"));
            parkingSpotCache.put(id, parkingSpot);
            return parkingSpot;
        }
    }

    public Set<ParkingSpot> findAll() {
        return repository.findAll();
    }
}

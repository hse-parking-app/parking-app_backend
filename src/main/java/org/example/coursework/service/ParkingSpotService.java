package org.example.coursework.service;

import java.util.Set;
import java.util.UUID;

import org.example.coursework.dao.ParkingSpotRepository;
import org.example.coursework.error.NotFoundException;
import org.example.coursework.model.ParkingSpot;
import org.springframework.stereotype.Service;

import static org.example.coursework.utils.Cache.parkingSpotCache;

@Service
public class ParkingSpotService {
    private final ParkingSpotRepository repository;

    public ParkingSpotService(ParkingSpotRepository repository) {
        this.repository = repository;
    }

    public ParkingSpot save(ParkingSpot parkingSpot) {
        repository.save(parkingSpot);
        parkingSpotCache.remove(parkingSpot.getId());
        return find(parkingSpot.getId());
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

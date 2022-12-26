package org.hse.parkings.service;

import org.hse.parkings.dao.ReservationRepository;
import org.hse.parkings.exception.EngagedException;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.Reservation;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.reservationCache;

@Service
public class ReservationService {

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation save(Reservation reservation) throws EngagedException {
        boolean parkingSpotEngaged = !repository.getParkingSpotTimeCollisions(reservation).isEmpty();
        boolean carAlreadyHasParkingSpaceAtThisTime = !repository.getCarTimeCollisions(reservation).isEmpty();
        if (parkingSpotEngaged) {
            throw new EngagedException("The parking space is occupied. Try another parking spot or reservation time");
        }
        if (carAlreadyHasParkingSpaceAtThisTime) {
            throw new EngagedException("One car can occupy only one parking space at a time. Try another time or car");
        }
        Reservation toSave = Reservation.builder()
                .carId(reservation.getCarId())
                .employeeId(reservation.getEmployeeId())
                .carId(reservation.getCarId())
                .parkingSpotId(reservation.getParkingSpotId())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime()).build();
        repository.save(toSave);
        reservationCache.remove(toSave.getId());
        return find(toSave.getId());
    }

    public Reservation update(Reservation reservation) throws EngagedException {
        boolean parkingSpotEngaged = !repository.getParkingSpotTimeCollisions(reservation).isEmpty();
        boolean carAlreadyHasParkingSpaceAtThisTime = !repository.getCarTimeCollisions(reservation).isEmpty();
        if (parkingSpotEngaged) {
            throw new EngagedException("The parking space is occupied. Try another parking spot or reservation time");
        }
        if (carAlreadyHasParkingSpaceAtThisTime) {
            throw new EngagedException("The parking space is occupied. Try another parking spot or reservation time");
        }
        repository.update(reservation);
        reservationCache.remove(reservation.getId());
        return find(reservation.getId());
    }

    public void delete(UUID id) {
        repository.delete(id);
        reservationCache.remove(id);
    }

    public void deleteAll() {
        repository.deleteAll();
        reservationCache.clear();
    }

    public Reservation find(UUID id) throws NotFoundException {
        if (reservationCache.containsKey(id)) {
            return reservationCache.get(id);
        }
        Reservation reservation = repository
                .find(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id = " + id + " not found"));
        reservationCache.put(id, reservation);
        return reservation;
    }

    public Set<Reservation> findAll() {
        return repository.findAll();
    }
}

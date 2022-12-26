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
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation save(Reservation reservation) throws EngagedException {
        boolean parkingSpotEngaged = !reservationRepository.getParkingSpotTimeCollisions(reservation).isEmpty();
        boolean carAlreadyHasParkingSpaceAtThisTime = !reservationRepository.getCarTimeCollisions(reservation).isEmpty();
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
        reservationRepository.save(toSave);
        reservationCache.remove(toSave.getId());
        return find(toSave.getId());
    }

    public Reservation update(Reservation reservation) throws EngagedException {
        boolean parkingSpotEngaged = !reservationRepository.getParkingSpotTimeCollisions(reservation).isEmpty();
        boolean carAlreadyHasParkingSpaceAtThisTime = !reservationRepository.getCarTimeCollisions(reservation).isEmpty();
        if (parkingSpotEngaged) {
            throw new EngagedException("The parking space is occupied. Try another parking spot or reservation time");
        }
        if (carAlreadyHasParkingSpaceAtThisTime) {
            throw new EngagedException("The parking space is occupied. Try another parking spot or reservation time");
        }
        reservationRepository.update(reservation);
        reservationCache.remove(reservation.getId());
        return find(reservation.getId());
    }

    public void delete(UUID id) {
        reservationRepository.delete(id);
        reservationCache.remove(id);
    }

    public void deleteAll() {
        reservationRepository.deleteAll();
        reservationCache.clear();
    }

    public Reservation find(UUID id) throws NotFoundException {
        if (reservationCache.containsKey(id)) {
            return reservationCache.get(id);
        } else {
            Reservation reservation = reservationRepository
                    .find(id)
                    .orElseThrow(() -> new NotFoundException("Reservation with id = " + id + " not found"));
            reservationCache.put(id, reservation);
            return reservation;
        }
    }

    public Set<Reservation> findAll() {
        return reservationRepository.findAll();
    }
}

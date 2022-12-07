package org.example.coursework.service;

import java.util.Set;
import java.util.UUID;

import org.example.coursework.dao.ReservationRepository;
import org.example.coursework.error.EngagedException;
import org.example.coursework.error.NotFoundException;
import org.example.coursework.model.Reservation;
import org.springframework.stereotype.Service;

import static org.example.coursework.utils.Cache.reservationCache;

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
        reservationRepository.save(reservation);
        reservationCache.remove(reservation.getId());
        return find(reservation.getId());
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

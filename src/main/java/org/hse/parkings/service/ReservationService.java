package org.hse.parkings.service;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.dao.CarRepository;
import org.hse.parkings.dao.EmployeeRepository;
import org.hse.parkings.dao.ReservationRepository;
import org.hse.parkings.dao.building.ParkingSpotRepository;
import org.hse.parkings.exception.EngagedException;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.Car;
import org.hse.parkings.model.Reservation;
import org.hse.parkings.model.building.ParkingSpot;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.utils.DateTimeProvider;
import org.hse.parkings.utils.Log;
import org.hse.parkings.utils.PBQElement;
import org.hse.parkings.utils.Pair;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledFuture;

import static org.hse.parkings.utils.Cache.*;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final CarRepository carRepository;

    private final EmployeeRepository employeeRepository;

    private final ParkingSpotRepository parkingSpotRepository;

    private final TaskScheduler taskScheduler;

    private final DateTimeProvider dateTimeProvider;

    private final Validator validator;

    private final PriorityBlockingQueue<PBQElement> reservationsQueue = new PriorityBlockingQueue<>(100,
            (one, two) -> {
                int isEndTimeComp = one.getIsEndTime().compareTo(two.getIsEndTime());
                int timeComp = one.getExecutionTime().compareTo(two.getExecutionTime());
                if (isEndTimeComp == 0) {
                    return timeComp;
                }
                if (timeComp == 0) {
                    if (isEndTimeComp < 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
                return timeComp;
            });

    private ScheduledFuture<?> scheduleTask(Runnable task, Date time) {
        return taskScheduler.schedule(task, time);
    }

    private synchronized void executeOnTop() {
        PBQElement element = reservationsQueue.poll();
        if (element != null) {
            element.getRunnable().run();
        }
    }

    public Reservation save(Reservation reservation) throws EngagedException, NotFoundException {
        Reservation toSave = Reservation.builder()
                .carId(reservation.getCarId())
                .employeeId(reservation.getEmployeeId())
                .carId(reservation.getCarId())
                .parkingSpotId(reservation.getParkingSpotId())
                .startTime(reservation.getStartTime().truncatedTo(ChronoUnit.SECONDS))
                .endTime(reservation.getEndTime().truncatedTo(ChronoUnit.SECONDS)).build();

        Car car = carRepository.find(toSave.getCarId())
                .orElseThrow(() -> new NotFoundException("Car with id = " + toSave.getCarId() + " not found"));
        Employee employee = employeeRepository.findById(toSave.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee with id = " + toSave.getEmployeeId() + " not found"));
        ParkingSpot spot = parkingSpotRepository.find(toSave.getParkingSpotId())
                .orElseThrow(() -> new NotFoundException("ParkingSpot with id = " + toSave.getParkingSpotId() + " not found"));

        if (!spot.getIsAvailable()) {
            throw new NotFoundException("ParkingSpot with id = " + toSave.getParkingSpotId() + " not found");
        }

        if (!car.getOwnerId().equals(employee.getId())) {
            throw new NotFoundException("Car with id = " + toSave.getCarId() + " not found");
        }

        boolean parkingSpotEngaged = !reservationRepository.getParkingSpotTimeCollisions(toSave).isEmpty();
        boolean carAlreadyHasParkingSpaceAtThisTime = !reservationRepository.getCarTimeCollisions(toSave).isEmpty();
        if (parkingSpotEngaged) {
            throw new EngagedException("The parking space is occupied. Try another parking spot or reservation time");
        }
        if (carAlreadyHasParkingSpaceAtThisTime) {
            throw new EngagedException("One car can occupy only one parking space at a time. Try another time or car");
        }

        reservationsQueue.put(new PBQElement(toSave.getId(), false, toSave.getStartTime(), () -> {
            parkingSpotRepository.occupySpot(toSave.getParkingSpotId());
            parkingSpotCache.remove(toSave.getParkingSpotId());
        }));
        reservationsQueue.put(new PBQElement(toSave.getId(), true, toSave.getEndTime(), () -> {
            parkingSpotRepository.freeSpot(toSave.getParkingSpotId());
            parkingSpotCache.remove(toSave.getParkingSpotId());
            reservationRepository.delete(toSave.getId());
            reservationCache.remove(toSave.getId());
            scheduledTasksCache.remove(toSave.getId());
        }));
        scheduledTasksCache.put(toSave.getId(), new Pair<>(
                scheduleTask(this::executeOnTop,
                        Date.from(toSave.getStartTime().atZone(dateTimeProvider.getClock().getZone()).toInstant())),
                scheduleTask(this::executeOnTop,
                        Date.from(toSave.getEndTime().atZone(dateTimeProvider.getClock().getZone()).toInstant()))
        ));

        reservationRepository.save(toSave);
        reservationCache.remove(toSave.getId());
        return find(toSave.getId());
    }

    public Reservation extendReservation(UUID id, LocalDateTime endTime) throws EngagedException, NotFoundException {
        Reservation reservation = find(id);
        reservation.setEndTime(endTime.truncatedTo(ChronoUnit.SECONDS));

        Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        boolean parkingSpotEngaged = !reservationRepository.getParkingSpotTimeCollisions(reservation).isEmpty();
        boolean carAlreadyHasParkingSpaceAtThisTime = !reservationRepository.getCarTimeCollisions(reservation).isEmpty();
        if (parkingSpotEngaged) {
            throw new EngagedException("Can't extend. The parking space is occupied. Try another parking spot or reservation time");
        }
        if (carAlreadyHasParkingSpaceAtThisTime) {
            throw new EngagedException("One car can occupy only one parking space at a time. Try another time or car");
        }

        if (scheduledTasksCache.get(reservation.getId()).second().isDone()) {
            return find(reservation.getId());
        }

        scheduledTasksCache.get(reservation.getId()).second().cancel(true);
        reservationsQueue.removeIf(item -> item.getIsEndTime() && item.getId().equals(reservation.getId()));
        reservationsQueue.put(new PBQElement(reservation.getId(), true, reservation.getEndTime(), () -> {
            parkingSpotRepository.freeSpot(reservation.getParkingSpotId());
            parkingSpotCache.remove(reservation.getParkingSpotId());
            reservationRepository.delete(reservation.getId());
            reservationCache.remove(reservation.getId());
        }));
        scheduledTasksCache.put(reservation.getId(), new Pair<>(
                scheduledTasksCache.get(reservation.getId()).first(),
                scheduleTask(this::executeOnTop,
                        Date.from(reservation.getEndTime().atZone(dateTimeProvider.getClock().getZone()).toInstant()))
        ));

        reservationRepository.update(reservation);
        reservationCache.remove(reservation.getId());
        return find(reservation.getId());
    }

    public void delete(UUID id) throws NotFoundException {
        Reservation reservation = find(id);
        Pair<ScheduledFuture<?>, ScheduledFuture<?>> scheduled
                = scheduledTasksCache.get(id);
        if (scheduled.first().isDone()) {
            scheduled.second().cancel(true);
            parkingSpotRepository.freeSpot(reservation.getParkingSpotId());
        } else {
            scheduled.first().cancel(true);
            scheduled.second().cancel(true);
        }
        parkingSpotCache.remove(reservation.getParkingSpotId());
        scheduledTasksCache.remove(id);
        reservationsQueue.removeIf(item -> item.getId().equals(reservation.getId()));
        reservationRepository.delete(id);
        reservationCache.remove(id);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void deleteAll() {
        reservationRepository.deleteAll();
        parkingSpotRepository.freeAllParkingSpots();

        scheduledTasksCache.forEach((uuid, pair) -> {
            pair.first().cancel(true);
            pair.second().cancel(true);
        });
        reservationsQueue.clear();
        scheduledTasksCache.clear();
        reservationCache.clear();
        parkingSpotCache.clear();

        Log.logger.info("Reservations cleared, spots freed");
    }

    public Reservation find(UUID id) throws NotFoundException {
        if (reservationCache.containsKey(id)) {
            return reservationCache.get(id);
        }
        Reservation reservation = reservationRepository.find(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id = " + id + " not found"));
        reservationCache.put(id, reservation);
        return reservation;
    }

    public Set<Reservation> findEmployeeReservations(UUID employeeId) {
        return reservationRepository.findEmployeeReservations(employeeId);
    }

    public Set<Reservation> findAll() {
        return reservationRepository.findAll();
    }
}

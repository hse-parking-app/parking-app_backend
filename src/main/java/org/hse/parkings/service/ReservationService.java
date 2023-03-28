package org.hse.parkings.service;

import org.hse.parkings.dao.CarRepository;
import org.hse.parkings.dao.EmployeeRepository;
import org.hse.parkings.dao.ReservationRepository;
import org.hse.parkings.dao.building.ParkingSpotRepository;
import org.hse.parkings.exception.EngagedException;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.Car;
import org.hse.parkings.model.Employee;
import org.hse.parkings.model.Reservation;
import org.hse.parkings.utils.DateTimeProvider;
import org.hse.parkings.utils.Log;
import org.hse.parkings.utils.Pair;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import static org.hse.parkings.utils.Cache.*;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final CarRepository carRepository;

    private final EmployeeRepository employeeRepository;

    private final ParkingSpotRepository parkingSpotRepository;

    private final TaskScheduler taskScheduler;

    private final DateTimeProvider dateTimeProvider;

    private final Validator validator;

    public ReservationService(ReservationRepository reservationRepository,
                              CarRepository carRepository,
                              EmployeeRepository employeeRepository,
                              ParkingSpotRepository parkingSpotRepository,
                              TaskScheduler taskScheduler,
                              DateTimeProvider dateTimeProvider,
                              Validator validator) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
        this.employeeRepository = employeeRepository;
        this.parkingSpotRepository = parkingSpotRepository;
        this.taskScheduler = taskScheduler;
        this.dateTimeProvider = dateTimeProvider;
        this.validator = validator;
    }

    public ScheduledFuture<?> scheduleTask(Runnable task, Date time) {
        return taskScheduler.schedule(task, time);
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
        parkingSpotRepository.find(toSave.getParkingSpotId())
                .orElseThrow(() -> new NotFoundException("ParkingSpot with id = " + toSave.getParkingSpotId() + " not found"));

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

        scheduledTasksCache.put(toSave.getId(), new Pair<>(
                scheduleTask(() -> {
                            try {
                                find(toSave.getId());
                            } catch (NotFoundException e) {
                                return;
                            }
                            parkingSpotRepository.occupySpot(toSave.getParkingSpotId());
                            parkingSpotCache.remove(toSave.getParkingSpotId());
                        },
                        Date.from(toSave.getStartTime().atZone(dateTimeProvider.getClock().getZone()).toInstant())),
                scheduleTask(() -> {
                            try {
                                find(toSave.getId());
                            } catch (NotFoundException e) {
                                return;
                            }
                            parkingSpotRepository.freeSpot(toSave.getParkingSpotId());
                            parkingSpotCache.remove(toSave.getParkingSpotId());
                            reservationRepository.delete(toSave.getId());
                            reservationCache.remove(toSave.getId());
                        },
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
        scheduledTasksCache.put(reservation.getId(), new Pair<>(
                scheduledTasksCache.get(reservation.getId()).first(),
                scheduleTask(() -> {
                            try {
                                find(reservation.getId());
                            } catch (NotFoundException e) {
                                return;
                            }
                            parkingSpotRepository.freeSpot(reservation.getParkingSpotId());
                            parkingSpotCache.remove(reservation.getParkingSpotId());
                            reservationRepository.delete(reservation.getId());
                            reservationCache.remove(reservation.getId());
                        },
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
            scheduledTasksCache.remove(id);
        } else {
            scheduled.first().cancel(true);
            scheduled.second().cancel(true);
            scheduledTasksCache.remove(id);
        }
        parkingSpotCache.remove(reservation.getParkingSpotId());
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
        scheduledTasksCache.clear();
        reservationCache.clear();
        parkingSpotCache.clear();

        Log.logger.info("Reservations cleared, spots freed");
    }

    public Reservation find(UUID id) throws NotFoundException {
        if (reservationCache.containsKey(id)) {
            return reservationCache.get(id);
        }
        Reservation reservation = reservationRepository
                .find(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id = " + id + " not found"));
        reservationCache.put(id, reservation);
        return reservation;
    }

    public Set<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Scheduled(cron = "@daily")
    public void clearExpiredReservations() {
        reservationRepository.deleteExpiredReservations(dateTimeProvider.getZonedDateTime().toLocalDateTime());
        Log.logger.info("Expired reservations cleared");
    }
}

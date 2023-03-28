package org.hse.parkings.utils;

import org.hse.parkings.model.Car;
import org.hse.parkings.model.Employee;
import org.hse.parkings.model.Reservation;
import org.hse.parkings.model.building.Building;
import org.hse.parkings.model.building.ParkingLevel;
import org.hse.parkings.model.building.ParkingSpot;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class Cache {

    public static ConcurrentHashMap<UUID, Car> carCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Employee> employeeCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Reservation> reservationCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Building> buildingCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, ParkingLevel> parkingLevelCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, ParkingSpot> parkingSpotCache = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<UUID, Pair<ScheduledFuture<?>, ScheduledFuture<?>>> scheduledTasksCache = new ConcurrentHashMap<>();
}

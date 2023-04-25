package org.hse.parkings.utils;

import org.hse.parkings.model.Car;
import org.hse.parkings.model.Reservation;
import org.hse.parkings.model.building.Building;
import org.hse.parkings.model.building.ParkingLevel;
import org.hse.parkings.model.building.ParkingSpot;
import org.hse.parkings.model.employee.Employee;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class Cache {

    public static ConcurrentHashMap<UUID, Car> carCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Employee> employeeCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Reservation> reservationCache = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<UUID, Building> buildingCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Set<ParkingLevel>> buildingLevelsCache = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<UUID, ParkingLevel> parkingLevelCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Set<ParkingSpot>> parkingLevelSpotsCache = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<UUID, ParkingSpot> parkingSpotCache = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<UUID, Pair<ScheduledFuture<?>, ScheduledFuture<?>>> scheduledTasksCache
            = new ConcurrentHashMap<>();

    @Scheduled(cron = "@daily")
    public static void freeCache() {
        carCache.clear();

        employeeCache.clear();

        reservationCache.clear();

        buildingCache.clear();
        buildingLevelsCache.clear();
        parkingLevelCache.clear();
        parkingLevelSpotsCache.clear();
        parkingSpotCache.clear();

        Log.logger.info("Cache cleared");
    }
}

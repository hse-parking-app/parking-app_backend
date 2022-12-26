package org.hse.parkings.utils;

import org.hse.parkings.model.Car;
import org.hse.parkings.model.Employee;
import org.hse.parkings.model.ParkingSpot;
import org.hse.parkings.model.Reservation;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    public static ConcurrentHashMap<UUID, Car> carCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Employee> employeeCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, ParkingSpot> parkingSpotCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Reservation> reservationCache = new ConcurrentHashMap<>();
}

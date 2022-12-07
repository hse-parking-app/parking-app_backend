package org.example.coursework.utils;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.example.coursework.model.Car;
import org.example.coursework.model.Employee;
import org.example.coursework.model.ParkingSpot;
import org.example.coursework.model.Reservation;

public class Cache {
    public static ConcurrentHashMap<UUID, Car> carCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Employee> employeeCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, ParkingSpot> parkingSpotCache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Reservation> reservationCache = new ConcurrentHashMap<>();
}

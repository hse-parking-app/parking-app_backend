/*
 * Copyright (c) 2021-2021.
 * Written by Maksim Stepanenko <stepanenko-qa@yandex.ru>
 */

package org.example.coursework;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.coursework.model.Car;
import org.example.coursework.model.Employee;
import org.example.coursework.model.ParkingSpot;
import org.example.coursework.model.Reservation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.example.coursework.utils.Cache.carCache;
import static org.example.coursework.utils.Cache.employeeCache;
import static org.example.coursework.utils.Cache.parkingSpotCache;
import static org.example.coursework.utils.Cache.reservationCache;

@SpringBootTest
@AutoConfigureMockMvc
public class AbstractTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    protected ObjectMapper jackson = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    void insert(Car car) {
        jdbcTemplate.update("""
                INSERT INTO cars (id, model, dimension_length, dimension_wight, registry_number)
                VALUES (?, ?, ?, ?, ?)
                """, ps -> {
            ps.setObject(1, car.getId());
            ps.setString(2, car.getModel());
            ps.setInt(3, car.getLength());
            ps.setInt(4, car.getWight());
            ps.setString(5, car.getRegistryNumber());
        });
    }

    void insert(Employee employee) {
        jdbcTemplate.update("""
                INSERT INTO employees (id, name)
                VALUES (?, ?)
                """, ps -> {
            ps.setObject(1, employee.getId());
            ps.setString(2, employee.getName());
        });
    }

    void insert(ParkingSpot parkingSpot) {
        jdbcTemplate.update("""
                INSERT INTO parking_spots (id, parking_number, is_free)
                VALUES (?, ?, ?)
                """, ps -> {
            ps.setObject(1, parkingSpot.getId());
            ps.setInt(2, parkingSpot.getParkingNumber());
            ps.setBoolean(3, parkingSpot.getIsFree());
        });
    }

    void insert(Reservation reservation) {
        jdbcTemplate.update("""
                INSERT INTO reservations (id, car_id, employee_id, parking_spot_id, start_time, end_time)
                VALUES (?, ?, ?, ?, ?, ?)
                """, ps -> {
            ps.setObject(1, reservation.getId());
            ps.setObject(2, reservation.getCarId());
            ps.setObject(3, reservation.getEmployeeId());
            ps.setObject(4, reservation.getParkingSpotId());
            ps.setObject(5, reservation.getStartTime());
            ps.setObject(6, reservation.getEndTime());
        });
    }

    void deleteCars() {
        jdbcTemplate.update("DELETE FROM cars");
    }

    void deleteEmployees() {
        jdbcTemplate.update("DELETE FROM employees");
    }

    void deleteParkingSpots() {
        jdbcTemplate.update("DELETE FROM parking_spots");
    }

    void deleteReservations() {
        jdbcTemplate.update("DELETE FROM reservations");
    }

    protected final Car car = new Car("Supra", 5, 2, "5adm799");
    protected final Employee employee = new Employee("Pupa");
    protected final ParkingSpot parkingSpot = new ParkingSpot(1, true);
    protected final Reservation reservation = new Reservation(
            car.getId(),
            employee.getId(),
            parkingSpot.getId(),
            LocalDateTime.of(2031, 1, 1, 0, 0, 0),
            LocalDateTime.of(2031, 1, 1, 1, 0, 0)
    );

    @BeforeEach
    void setUpEach() {
        insert(car);
        insert(employee);
        insert(parkingSpot);
        insert(reservation);
    }

    @AfterEach
    void tearDownEach() {
        deleteCars();
        deleteEmployees();
        deleteParkingSpots();
        deleteReservations();
        carCache.clear();
        employeeCache.clear();
        parkingSpotCache.clear();
        reservationCache.clear();
    }
}

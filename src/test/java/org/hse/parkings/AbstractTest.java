package org.hse.parkings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hse.parkings.model.Car;
import org.hse.parkings.model.Employee;
import org.hse.parkings.model.Reservation;
import org.hse.parkings.model.building.ParkingSpot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AbstractTest {

    protected final Car carSupra = Car.builder()
            .id(UUID.randomUUID())
            .model("Supra")
            .lengthMeters(1.43)
            .weightTons(1.3333)
            .registryNumber("ac343y152").build();

    protected final Employee employee = Employee.builder()
            .id(UUID.randomUUID())
            .name("Pupa").build();
    protected final ParkingSpot parkingSpot = ParkingSpot.builder()
            .parkingNumber("1B")
            .isFree(true).build();
    protected final Reservation reservation = Reservation.builder()
            .carId(carSupra.getId())
            .employeeId(employee.getId())
            .parkingSpotId(parkingSpot.getId())
            .startTime(LocalDateTime.of(2031, 1, 1, 0, 0, 0))
            .endTime(LocalDateTime.of(2031, 1, 1, 1, 0, 0)).build();

    @Autowired
    protected MockMvc mockMvc;
    protected ObjectMapper jackson = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    void insert(Car car) {
        jdbcTemplate.update(
                """
                        INSERT INTO cars (id, model, length_meters, weight_tons, registry_number)
                        VALUES (?, ?, ?, ?, ?)
                        """, ps -> {
                    ps.setObject(1, car.getId());
                    ps.setString(2, car.getModel());
                    ps.setDouble(3, car.getLengthMeters());
                    ps.setDouble(4, car.getWeightTons());
                    ps.setString(5, car.getRegistryNumber());
                });
    }

//    void insert(Employee employee) {
//        jdbcTemplate.update(
//                """
//                        INSERT INTO employees (id, name)
//                        VALUES (?, ?)
//                        """, ps -> {
//                    ps.setObject(1, employee.getId());
//                    ps.setString(2, employee.getName());
//                });
//    }
//
//    void insert(ParkingSpot parkingSpot) {
//        jdbcTemplate.update(
//                """
//                        INSERT INTO parking_spots (id, parking_number, is_free)
//                        VALUES (?, ?, ?)
//                        """, ps -> {
//                    ps.setObject(1, parkingSpot.getId());
//                    ps.setString(2, parkingSpot.getParkingNumber());
//                    ps.setBoolean(3, parkingSpot.getIsFree());
//                });
//    }
//
//    void insert(Reservation reservation) {
//        jdbcTemplate.update(
//                """
//                        INSERT INTO reservations (id, car_id, employee_id, parking_spot_id, start_time, end_time)
//                        VALUES (?, ?, ?, ?, ?, ?)
//                        """, ps -> {
//                    ps.setObject(1, reservation.getId());
//                    ps.setObject(2, reservation.getCarId());
//                    ps.setObject(3, reservation.getEmployeeId());
//                    ps.setObject(4, reservation.getParkingSpotId());
//                    ps.setObject(5, reservation.getStartTime());
//                    ps.setObject(6, reservation.getEndTime());
//                });
//    }

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

    @BeforeEach
    void setUpEach() {
        insert(carSupra);
//        insert(employee);
//        insert(parkingSpot);
//        insert(reservation);
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

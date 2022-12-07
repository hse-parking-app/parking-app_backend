package org.example.coursework.logic;

import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.coursework.AbstractTest;
import org.example.coursework.dao.CarRepository;
import org.example.coursework.model.Car;
import org.example.coursework.model.Employee;
import org.example.coursework.model.ParkingSpot;
import org.example.coursework.model.Reservation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class ReservationConstraintTests extends AbstractTest {
    @Autowired
    CarRepository repository;

    final Reservation reservation2 = new Reservation(
            car.getId(),
            employee.getId(),
            parkingSpot.getId(),
            LocalDateTime.of(2031, 1, 1, 12, 0, 0),
            LocalDateTime.of(2031, 1, 1, 13, 0, 0)
    );
    String reservationString;
    String reservation2String;

    {
        try {
            reservationString = jackson.writeValueAsString(reservation);
            reservation2String = jackson.writeValueAsString(reservation2);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void whenReservationDurationOnWeekend_thenBadRequest() {

    }

    @Test
    public void whenDurationIsMoreThan24Hours_thenBadRequest() {

    }

    @Test
    public void givenDifferentEmployee_whenBookEngagedTime_thenEngagedException() throws Exception {
        final Car car = new Car("Celica", 4, 2, "785das");
        String car2String = jackson.writeValueAsString(car);
        this.mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(car2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(car2String)));

        final Employee employee = new Employee("Buba");
        String employee2String = jackson.writeValueAsString(employee);
        this.mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employee2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(employee2String)));

        final ParkingSpot parkingSpot = new ParkingSpot(2, true);
        String parkingSpot2String = jackson.writeValueAsString(parkingSpot);
        this.mockMvc.perform(post("/parkingSpots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parkingSpot2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(parkingSpot2String)));

        final Reservation reservation = new Reservation(
                car.getId(),
                employee.getId(),
                parkingSpot.getId(),
                LocalDateTime.of(2031, 1, 1, 0, 1, 0),
                LocalDateTime.of(2031, 1, 1, 0, 59, 0)
        );
        reservation2String = jackson.writeValueAsString(reservation);
        this.mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservation2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(reservation2String)));
    }

}

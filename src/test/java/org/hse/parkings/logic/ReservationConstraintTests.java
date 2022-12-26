package org.hse.parkings.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hse.parkings.AbstractTest;
import org.hse.parkings.dao.CarRepository;
import org.hse.parkings.model.Car;
import org.hse.parkings.model.Employee;
import org.hse.parkings.model.Reservation;
import org.hse.parkings.model.building.ParkingSpot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class ReservationConstraintTests extends AbstractTest {

    //    final Reservation reservation2 = new Reservation(
//            car.getId(),
//            employee.getId(),
//            parkingSpot.getId(),
//            LocalDateTime.of(2031, 1, 1, 12, 0, 0),
//            LocalDateTime.of(2031, 1, 1, 13, 0, 0)
//    );
    final Reservation reservation2 = Reservation.builder()
            .carId(carSupra.getId())
            .employeeId(employee.getId())
            .parkingSpotId(parkingSpot.getId())
            .startTime(LocalDateTime.of(2031, 1, 1, 12, 0, 0))
            .endTime(LocalDateTime.of(2031, 1, 1, 13, 0, 0)).build();
    @Autowired
    CarRepository repository;
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
//        final Car car = new Car("Celica", 4, 2, "785das");
        final Car car = Car.builder()
                .model("Celica")
                .lengthMeters(4.0)
                .weightTons(2.0)
                .registryNumber("785das").build();
        String car2String = jackson.writeValueAsString(car);
        this.mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(car2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(car2String)));

//        final Employee employee = new Employee("Buba");
        final Employee employee = Employee.builder()
                .name("Buba").build();
        String employee2String = jackson.writeValueAsString(employee);
        this.mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employee2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(employee2String)));

//        final ParkingSpot parkingSpot = new ParkingSpot(2, true);
        final ParkingSpot parkingSpot = ParkingSpot.builder()
                .parkingNumber("2")
                .isFree(true)
                .build();
        String parkingSpot2String = jackson.writeValueAsString(parkingSpot);
        this.mockMvc.perform(post("/parkingSpots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parkingSpot2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(parkingSpot2String)));

//        final Reservation reservation = new Reservation(
//                car.getId(),
//                employee.getId(),
//                parkingSpot.getId(),
//                LocalDateTime.of(2031, 1, 1, 0, 1, 0),
//                LocalDateTime.of(2031, 1, 1, 0, 59, 0)
//        );
        final Reservation reservation = Reservation.builder()
                .carId(car.getId())
                .employeeId(employee.getId())
                .parkingSpotId(parkingSpot.getId())
                .startTime(LocalDateTime.of(2031, 1, 1, 0, 1, 0))
                .endTime(LocalDateTime.of(2031, 1, 1, 0, 59, 0)).build();
        reservation2String = jackson.writeValueAsString(reservation);
        this.mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservation2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(reservation2String)));
    }

}

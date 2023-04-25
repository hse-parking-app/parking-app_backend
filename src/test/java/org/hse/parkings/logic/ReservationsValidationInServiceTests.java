package org.hse.parkings.logic;

import com.jayway.jsonpath.JsonPath;
import org.hse.parkings.AbstractTest;
import org.hse.parkings.model.Car;
import org.hse.parkings.model.Reservation;
import org.hse.parkings.model.building.ParkingSpot;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.utils.DateTimeProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.scheduledTasksCache;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WithMockUser(username = "admin", roles = {"ADMIN"})
@DisplayName("Reservations validation in service")
public class ReservationsValidationInServiceTests extends AbstractTest {

    private final String endpoint = "/reservations";

    @AfterEach
    void restoreTime() {
        dateTimeProvider.resetClock();
    }

    @Test
    @DisplayName("Reservation with not existing car")
    public void negative_saveReservationWithNotExistingCar() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        Car temp = carSupraOfAlice;
        temp.setId(UUID.randomUUID());

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(temp.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusHours(2)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("Reservation with not existing employee")
    public void negative_saveReservationWithNotExistingEmployee() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        Employee temp = employeeAlice;
        temp.setId(UUID.randomUUID());

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(temp.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusHours(2)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("Reservation with not existing parking spot")
    public void negative_saveReservationWithNotExistingParkingSpot() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        ParkingSpot temp = parkingSpotA;
        temp.setId(UUID.randomUUID());

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(temp.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusHours(2)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("Reservation with existing someone else's car")
    public void negative_saveReservationWithNotYourCar() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carAudiOfBob.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusHours(2)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("Fail validation on extend")
    public void positive_extendReservationFailValidation() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusHours(2)).build();

        MvcResult resultPost = this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()))
                .andReturn();
        Assertions.assertEquals(scheduledTasksCache.size(), 1);

        String id = JsonPath.read(resultPost.getResponse().getContentAsString(), "$.id");
        this.mockMvc.perform(put(endpoint + "/" + id)
                        .param("endTime", String.valueOf(localDateTime.minusHours(3))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(scheduledTasksCache.size(), 1);
    }

    @Test
    @DisplayName("User reserves two spots for one car")
    public void negative_userReservesMultipleSpotsForOneCar() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotB.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("User reserves one spot for two cars")
    public void negative_userReservesOneSpotForTwoCars() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carSkylineOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("User reserves two spots for two cars")
    public void positive_userReservesTwoSpotsForTwoCars() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carSkylineOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotB.getId())
                .startTime(localDateTime.plusSeconds(20))
                .endTime(localDateTime.plusSeconds(30)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservationTwo.getCarId().toString()));
    }

    @Test
    @DisplayName("Two users reserve one spot for their cars")
    public void positive_twoUsersReserveOneSpotForTheirCars() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carTeslaOfBob.getId())
                .employeeId(employeeBob.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(20))
                .endTime(localDateTime.plusSeconds(30)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservationTwo.getCarId().toString()));
    }

    @Test
    @DisplayName("Two users reserve one spot for the same car")
    public void negative_twoUsersReserveOneSpotForTheSameCar() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeBob.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(20))
                .endTime(localDateTime.plusSeconds(30)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Users reserves already reserved spot")
    public void negative_usersReservesAlreadyReservedSpot() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carTeslaOfBob.getId())
                .employeeId(employeeBob.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(4))
                .endTime(localDateTime.plusSeconds(6)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("User reserves the spot he has already reserved")
    public void negative_userReservesTheSpotHeHasAlreadyReserved() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(4))
                .endTime(localDateTime.plusSeconds(6)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("User reserves next time interval to his previous reservation")
    public void positive_userReservesNextTimeIntervalToHisPreviousReservation() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(10))
                .endTime(localDateTime.plusSeconds(15)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservationTwo.getCarId().toString()));
    }

    @Test
    @DisplayName("User reserves next time interval to reservation")
    public void positive_userReservesNextTimeIntervalToReservation() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carAudiOfBob.getId())
                .employeeId(employeeBob.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(10))
                .endTime(localDateTime.plusSeconds(15)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservationTwo.getCarId().toString()));
    }

    @Test
    @DisplayName("User reserves next time interval to his previous reservation on another spot")
    public void positive_userReservesNextTimeIntervalToHisPreviousReservationOnAnotherSpot() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotB.getId())
                .startTime(localDateTime.plusSeconds(10))
                .endTime(localDateTime.plusSeconds(15)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservationTwo.getCarId().toString()));
    }

    @Test
    @DisplayName("User reserves next time interval to reservation on another spot")
    public void positive_userReservesNextTimeIntervalToReservationOnAnotherSpot() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carTeslaOfBob.getId())
                .employeeId(employeeBob.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotB.getId())
                .startTime(localDateTime.plusSeconds(10))
                .endTime(localDateTime.plusSeconds(15)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservationTwo.getCarId().toString()));
    }

    @Test
    @DisplayName("User reserves unavailable spot")
    public void positive_userReservesUnavailableSpot() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime localDateTime = DateTimeProvider.getInstance().getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carTeslaOfBob.getId())
                .employeeId(employeeBob.getId())
                .parkingSpotId(parkingSpotC.getId())
                .startTime(localDateTime.plusSeconds(5))
                .endTime(localDateTime.plusSeconds(10)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}

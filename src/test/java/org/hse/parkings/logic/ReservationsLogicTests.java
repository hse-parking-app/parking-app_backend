package org.hse.parkings.logic;

import com.jayway.jsonpath.JsonPath;
import org.hse.parkings.AbstractTest;
import org.hse.parkings.dao.building.ParkingSpotRepository;
import org.hse.parkings.model.Reservation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.web.servlet.MvcResult;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("Reservations tests")
public class ReservationsLogicTests extends AbstractTest {

    private final String endpoint = "/reservations";

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @AfterEach
    void restoreTime() {
        dateTimeProvider.resetClock();

        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();
    }

    @Test
    @DisplayName("Validation of parking spot occupation")
    public void positive_validateParkingSpotOccupation() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();

        LocalDateTime localDateTime = dateTimeProvider.getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(3))
                .endTime(localDateTime.plusSeconds(6)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        await().atMost(4, TimeUnit.SECONDS).until(() ->
                !parkingSpotRepository.find(reservation.getParkingSpotId()).get().getIsFree());

        await().atMost(4, TimeUnit.SECONDS).until(() ->
                parkingSpotRepository.find(reservation.getParkingSpotId()).get().getIsFree());
    }

    @Test
    @DisplayName("Validation of two parking spot occupation at the same time")
    public void positive_validateTwoParkingSpotOccupationAtTheSameTime() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();

        LocalDateTime localDateTime = dateTimeProvider.getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(3))
                .endTime(localDateTime.plusSeconds(6)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carTeslaOfBob.getId())
                .employeeId(employeeBob.getId())
                .parkingSpotId(parkingSpotB.getId())
                .startTime(localDateTime.plusSeconds(3))
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
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservationTwo.getCarId().toString()));

        await().atMost(4, TimeUnit.SECONDS).until(() ->
                !parkingSpotRepository.find(reservation.getParkingSpotId()).get().getIsFree() &&
                        !parkingSpotRepository.find(reservationTwo.getParkingSpotId()).get().getIsFree());

        await().atMost(4, TimeUnit.SECONDS).until(() ->
                parkingSpotRepository.find(reservation.getParkingSpotId()).get().getIsFree() &&
                        parkingSpotRepository.find(reservationTwo.getParkingSpotId()).get().getIsFree());
    }

    @Test
    @DisplayName("Validate sequential occupation and freeing of different spots")
    public void positive_validateSequentialOccupationAndFreeingOfSpots() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();

        LocalDateTime localDateTime = dateTimeProvider.getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(3))
                .endTime(localDateTime.plusSeconds(9)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carTeslaOfBob.getId())
                .employeeId(employeeBob.getId())
                .parkingSpotId(parkingSpotB.getId())
                .startTime(localDateTime.plusSeconds(6))
                .endTime(localDateTime.plusSeconds(12)).build();

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

        await().atMost(4, TimeUnit.SECONDS).until(() ->
                !parkingSpotRepository.find(reservation.getParkingSpotId()).get().getIsFree());

        await().atMost(4, TimeUnit.SECONDS).until(() ->
                !parkingSpotRepository.find(reservationTwo.getParkingSpotId()).get().getIsFree());

        await().atMost(4, TimeUnit.SECONDS).until(() ->
                parkingSpotRepository.find(reservation.getParkingSpotId()).get().getIsFree());

        await().atMost(4, TimeUnit.SECONDS).until(() ->
                parkingSpotRepository.find(reservationTwo.getParkingSpotId()).get().getIsFree());
    }

    @Test
    @DisplayName("Validate reservation and another reservation on next time interval")
    public void positive_validationReservationAndAnotherReservationOnNextTimeInterval() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();


        LocalDateTime localDateTime = dateTimeProvider.getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(3))
                .endTime(localDateTime.plusSeconds(6)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(6))
                .endTime(localDateTime.plusSeconds(9)).build();

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

        await().atMost(4, TimeUnit.SECONDS).until(() ->
                !parkingSpotRepository.find(reservation.getParkingSpotId()).get().getIsFree());

        await().pollDelay(3, TimeUnit.SECONDS).untilAsserted(() -> Assertions.assertTrue(true));

        await().atMost(2, TimeUnit.SECONDS).until(() ->
                !parkingSpotRepository.find(reservationTwo.getParkingSpotId()).get().getIsFree());

        await().pollDelay(3, TimeUnit.SECONDS).untilAsserted(() -> Assertions.assertTrue(true));

        await().atMost(2, TimeUnit.SECONDS).until(() ->
                parkingSpotRepository.find(reservationTwo.getParkingSpotId()).get().getIsFree());
    }

    @Test
    @DisplayName("Validate first user reservation and second user reservation on next time interval")
    public void positive_validationFirstUserReservationAndAnotherSecondUserReservationOnNextTimeInterval() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();

        LocalDateTime localDateTime = dateTimeProvider.getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(3))
                .endTime(localDateTime.plusSeconds(6)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carTeslaOfBob.getId())
                .employeeId(employeeBob.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(6))
                .endTime(localDateTime.plusSeconds(9)).build();

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

        await().atMost(4, TimeUnit.SECONDS).until(() ->
                !parkingSpotRepository.find(reservation.getParkingSpotId()).get().getIsFree());

        await().pollDelay(3, TimeUnit.SECONDS).untilAsserted(() -> Assertions.assertTrue(true));

        await().atMost(2, TimeUnit.SECONDS).until(() ->
                !parkingSpotRepository.find(reservationTwo.getParkingSpotId()).get().getIsFree());

        await().pollDelay(3, TimeUnit.SECONDS).untilAsserted(() -> Assertions.assertTrue(true));

        await().atMost(2, TimeUnit.SECONDS).until(() ->
                parkingSpotRepository.find(reservationTwo.getParkingSpotId()).get().getIsFree());
    }

    @Test
    @DisplayName("Validation of parking spot occupation and freeing when it extended")
    public void positive_validateParkingSpotOccupationAndFreeingWhenExtend() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();

        LocalDateTime localDateTime = dateTimeProvider.getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(3))
                .endTime(localDateTime.plusSeconds(6)).build();

        MvcResult resultPost = this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()))
                .andReturn();

        String id = JsonPath.read(resultPost.getResponse().getContentAsString(), "$.id");
        this.mockMvc.perform(put(endpoint + "/" + id)
                        .param("endTime", String.valueOf(localDateTime.plusSeconds(8))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()))
                .andExpect(jsonPath("$.endTime").value(localDateTime.plusSeconds(8)
                        .truncatedTo(ChronoUnit.SECONDS).toString()));

        await().atMost(4, TimeUnit.SECONDS).until(() ->
                !parkingSpotRepository.find(reservation.getParkingSpotId()).get().getIsFree());

        await().atLeast(4, TimeUnit.SECONDS).until(() ->
                parkingSpotRepository.find(reservation.getParkingSpotId()).get().getIsFree());
    }
}

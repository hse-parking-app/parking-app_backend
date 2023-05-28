package org.hse.parkings.controller.appuser;

import com.jayway.jsonpath.JsonPath;
import org.hse.parkings.AbstractTest;
import org.hse.parkings.dao.building.ParkingSpotRepository;
import org.hse.parkings.model.Reservation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.web.servlet.MvcResult;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("AppUser Reservation controller")
public class AppUserReservationControllerTests extends AbstractTest {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    protected ThreadPoolTaskScheduler scheduler;

    @BeforeAll
    void userForTests() throws Exception {
        loginAs(employeeDarlene);
    }

    @AfterEach
    void restoreTime() {
        dateTimeProvider.resetClock();

        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();
    }

    @Test
    @DisplayName("POST AppUser - Save reservation and validate")
    public void positive_appUserReservationSaveAndValidate() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();

        LocalDateTime localDateTime = dateTimeProvider.getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carPorscheOfDarlene.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(3))
                .endTime(localDateTime.plusSeconds(6)).build();

        this.mockMvc.perform(post(reservationsEndpoint + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
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
    @DisplayName("PUT AppUser - Extend reservation and validate")
    public void positive_appUserReservationExtendAndValidate() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();

        LocalDateTime localDateTime = dateTimeProvider.getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carPorscheOfDarlene.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(3))
                .endTime(localDateTime.plusSeconds(6)).build();

        MvcResult resultPost = this.mockMvc.perform(post(reservationsEndpoint + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()))
                .andReturn();

        String id = JsonPath.read(resultPost.getResponse().getContentAsString(), "$.id");
        this.mockMvc.perform(put(reservationsEndpoint + "/" + id + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
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

    @Test
    @DisplayName("PUT DELETE AppUser - Extend foreign reservation")
    public void negative_appUserExtendForeignReservation() throws Exception {
        loginAs(employeeAlice);

        adjustClockTo(DayOfWeek.MONDAY);
        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();

        LocalDateTime localDateTime = dateTimeProvider.getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(3))
                .endTime(localDateTime.plusSeconds(6)).build();

        MvcResult resultPost = this.mockMvc.perform(post(reservationsEndpoint + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()))
                .andReturn();

        loginAs(employeeDarlene);

        String id = JsonPath.read(resultPost.getResponse().getContentAsString(), "$.id");
        this.mockMvc.perform(put(reservationsEndpoint + "/" + id + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .param("endTime", String.valueOf(localDateTime.plusSeconds(8))))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(delete(reservationsEndpoint + "/" + id + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET AppUser - Get reservations")
    public void positive_appUserGetReservation() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();

        LocalDateTime localDateTime = dateTimeProvider.getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carPorscheOfDarlene.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(3))
                .endTime(localDateTime.plusSeconds(6)).build();

        this.mockMvc.perform(post(reservationsEndpoint + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(get(reservationsEndpoint + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].carId").exists())
                .andExpect(jsonPath("$.[0].employeeId").exists())
                .andExpect(jsonPath("$.[0].parkingSpotId").exists())
                .andExpect(jsonPath("$.[0].startTime").exists())
                .andExpect(jsonPath("$.[0].endTime").exists());
    }

    @Test
    @DisplayName("DELETE AppUser - Delete reservation and validate")
    public void positive_validateCancelledReservations() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();

        LocalDateTime localDateTime = dateTimeProvider.getZonedDateTime().toLocalDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carPorscheOfDarlene.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(localDateTime.plusSeconds(3))
                .endTime(localDateTime.plusSeconds(6)).build();

        MvcResult resultPost = this.mockMvc.perform(post(reservationsEndpoint + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()))
                .andReturn();

        await().atMost(4, TimeUnit.SECONDS).until(() ->
                !parkingSpotRepository.find(reservation.getParkingSpotId()).get().getIsFree());

        String id = JsonPath.read(resultPost.getResponse().getContentAsString(), "$.id");
        this.mockMvc.perform(delete(reservationsEndpoint + "/" + id + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isOk());

        await().atMost(1, TimeUnit.SECONDS).until(() ->
                parkingSpotRepository.find(reservation.getParkingSpotId()).get().getIsFree());
    }
}

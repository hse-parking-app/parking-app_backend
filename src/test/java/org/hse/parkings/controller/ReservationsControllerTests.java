package org.hse.parkings.controller;

import com.jayway.jsonpath.JsonPath;
import org.hse.parkings.AbstractTest;
import org.hse.parkings.model.Reservation;
import org.hse.parkings.utils.DateTimeProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.scheduledTasksCache;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WithMockUser(username = "admin", roles = {"ADMIN"})
@DisplayName("Reservations controller")
public class ReservationsControllerTests extends AbstractTest {

    private final String endpoint = "/reservations";
    private final DateTimeProvider dateTimeProvider = DateTimeProvider.getInstance();
    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @AfterEach
    void restoreTime() {
        dateTimeProvider.resetClock();

        scheduler.setClock(dateTimeProvider.getClock());
        scheduler.initialize();
    }

    @Test
    @DisplayName("POST - Reservation")
    public void positive_saveReservation() throws Exception {
        DayOfWeek dayOfWeek = dateTimeProvider.getZonedDateTime().getDayOfWeek();
        int i = 0;
        while (dayOfWeek != DayOfWeek.MONDAY) {
            i++;
            dayOfWeek = dayOfWeek.plus(1);
        }
        dateTimeProvider.offsetClock(Duration.ofDays(i));
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        ZonedDateTime zonedDateTime = DateTimeProvider.getInstance().getZonedDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(zonedDateTime.toLocalDateTime().plusSeconds(5))
                .endTime(zonedDateTime.toLocalDateTime().plusSeconds(10)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));
        Assertions.assertEquals(scheduledTasksCache.size(), 1);
    }

    @Test
    @DisplayName("POST - Reservation with startTime in past")
    public void negative_saveReservationStartTimeInPast() throws Exception {
        ZonedDateTime zonedDateTime = DateTimeProvider.getInstance().getZonedDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(zonedDateTime.toLocalDateTime().minusSeconds(30))
                .endTime(zonedDateTime.toLocalDateTime().plusSeconds(10)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST - Reservation on more than 24 hours")
    public void negative_saveReservationOnMoreThan24Hours() throws Exception {
        DayOfWeek dayOfWeek = dateTimeProvider.getZonedDateTime().getDayOfWeek();
        int i = 0;
        while (dayOfWeek != DayOfWeek.MONDAY) {
            i++;
            dayOfWeek = dayOfWeek.plus(1);
        }
        dateTimeProvider.offsetClock(Duration.ofDays(i));
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        ZonedDateTime zonedDateTime = DateTimeProvider.getInstance().getZonedDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(zonedDateTime.toLocalDateTime().plusSeconds(5))
                .endTime(zonedDateTime.toLocalDateTime().plusDays(1).plusHours(1)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages.length()").value(2));
    }

    @Test
    @DisplayName("POST - Reservation on weekends")
    public void negative_saveReservationOnWeekends() throws Exception {
        DayOfWeek dayOfWeek = dateTimeProvider.getZonedDateTime().getDayOfWeek();
        int i = 0;
        while (dayOfWeek != DayOfWeek.SUNDAY) {
            i++;
            dayOfWeek = dayOfWeek.plus(1);
        }
        dateTimeProvider.offsetClock(Duration.ofDays(i));
        dateTimeProvider.offsetClock(Duration.ofHours(12 - dateTimeProvider.getZonedDateTime().getHour()));

        ZonedDateTime zonedDateTime = DateTimeProvider.getInstance().getZonedDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(zonedDateTime.toLocalDateTime().plusSeconds(5))
                .endTime(zonedDateTime.toLocalDateTime().plusSeconds(10)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages.length()").value(2));
    }

    @Test
    @DisplayName("POST - Reservation with day collision")
    public void negative_saveReservationWithDayCollision() throws Exception {
        DayOfWeek dayOfWeek = dateTimeProvider.getZonedDateTime().getDayOfWeek();
        int i = 0;
        while (dayOfWeek != DayOfWeek.MONDAY) {
            i++;
            dayOfWeek = dayOfWeek.plus(1);
        }
        dateTimeProvider.offsetClock(Duration.ofDays(i));
        dateTimeProvider.offsetClock(Duration.ofHours(23 - dateTimeProvider.getZonedDateTime().getHour()));

        ZonedDateTime zonedDateTime = DateTimeProvider.getInstance().getZonedDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(zonedDateTime.toLocalDateTime().plusSeconds(5))
                .endTime(zonedDateTime.toLocalDateTime().plusHours(2)).build();

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages.length()").value(2));
    }

    @Test
    @DisplayName("PUT - Extend existing reservation")
    public void positive_extendReservation() throws Exception {
        DayOfWeek dayOfWeek = dateTimeProvider.getZonedDateTime().getDayOfWeek();
        int i = 0;
        while (dayOfWeek != DayOfWeek.MONDAY) {
            i++;
            dayOfWeek = dayOfWeek.plus(1);
        }
        dateTimeProvider.offsetClock(Duration.ofDays(i));
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        ZonedDateTime zonedDateTime = DateTimeProvider.getInstance().getZonedDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(zonedDateTime.toLocalDateTime().plusSeconds(5))
                .endTime(zonedDateTime.toLocalDateTime().plusHours(2)).build();

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
                        .param("endTime", String.valueOf(zonedDateTime.toLocalDateTime().plusHours(3))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()))
                .andExpect(jsonPath("$.endTime").value(zonedDateTime.toLocalDateTime().plusHours(3)
                        .truncatedTo(ChronoUnit.SECONDS).toString()));
        Assertions.assertEquals(scheduledTasksCache.size(), 1);
    }

    @Test
    @DisplayName("PUT - Extend existing reservation to overlap other reservation")
    public void negative_extendReservationOverlapOtherReservation() throws Exception {
        DayOfWeek dayOfWeek = dateTimeProvider.getZonedDateTime().getDayOfWeek();
        int i = 0;
        while (dayOfWeek != DayOfWeek.MONDAY) {
            i++;
            dayOfWeek = dayOfWeek.plus(1);
        }
        dateTimeProvider.offsetClock(Duration.ofDays(i));
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        ZonedDateTime zonedDateTime = DateTimeProvider.getInstance().getZonedDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(zonedDateTime.toLocalDateTime().plusSeconds(5))
                .endTime(zonedDateTime.toLocalDateTime().plusHours(2)).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carAudiOfBob.getId())
                .employeeId(employeeBob.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(zonedDateTime.toLocalDateTime().plusHours(2))
                .endTime(zonedDateTime.toLocalDateTime().plusHours(4)).build();

        MvcResult resultPost = this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()))
                .andReturn();
        Assertions.assertEquals(scheduledTasksCache.size(), 1);

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservationTwo.getCarId().toString()));
        Assertions.assertEquals(scheduledTasksCache.size(), 2);

        String id = JsonPath.read(resultPost.getResponse().getContentAsString(), "$.id");
        this.mockMvc.perform(put(endpoint + "/" + id)
                        .param("endTime", String.valueOf(zonedDateTime.toLocalDateTime().plusHours(3))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(scheduledTasksCache.size(), 2);
    }

    @Test
    @DisplayName("GET - Reservation by id")
    public void positive_getReservationById() throws Exception {
        DayOfWeek dayOfWeek = dateTimeProvider.getZonedDateTime().getDayOfWeek();
        int i = 0;
        while (dayOfWeek != DayOfWeek.MONDAY) {
            i++;
            dayOfWeek = dayOfWeek.plus(1);
        }
        dateTimeProvider.offsetClock(Duration.ofDays(i));
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        ZonedDateTime zonedDateTime = DateTimeProvider.getInstance().getZonedDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(zonedDateTime.toLocalDateTime().plusSeconds(5))
                .endTime(zonedDateTime.toLocalDateTime().plusSeconds(10)).build();

        MvcResult resultPost = this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()))
                .andReturn();

        String id = JsonPath.read(resultPost.getResponse().getContentAsString(), "$.id");
        this.mockMvc.perform(get(endpoint + "/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));
        Assertions.assertEquals(scheduledTasksCache.size(), 1);
    }

    @Test
    @DisplayName("GET PUT DELETE - Not existing id Reservation")
    public void negative_getReservationNotExistingId() throws Exception {
        DayOfWeek dayOfWeek = dateTimeProvider.getZonedDateTime().getDayOfWeek();
        int i = 0;
        while (dayOfWeek != DayOfWeek.MONDAY) {
            i++;
            dayOfWeek = dayOfWeek.plus(1);
        }
        dateTimeProvider.offsetClock(Duration.ofDays(i));
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        ZonedDateTime zonedDateTime = DateTimeProvider.getInstance().getZonedDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(zonedDateTime.toLocalDateTime().plusSeconds(5))
                .endTime(zonedDateTime.toLocalDateTime().plusSeconds(10)).build();

        UUID uuid = UUID.randomUUID();
        this.mockMvc.perform(get(endpoint + "/" + uuid))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(put(endpoint + "/" + uuid)
                        .param("endTime", String.valueOf(zonedDateTime.toLocalDateTime().plusHours(3)))
                        .content(jackson.writeValueAsString(reservation))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(delete(endpoint + "/" + uuid))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE - Reservation")
    public void positive_getReservationNotExistingId() throws Exception {
        DayOfWeek dayOfWeek = dateTimeProvider.getZonedDateTime().getDayOfWeek();
        int i = 0;
        while (dayOfWeek != DayOfWeek.MONDAY) {
            i++;
            dayOfWeek = dayOfWeek.plus(1);
        }
        dateTimeProvider.offsetClock(Duration.ofDays(i));
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        ZonedDateTime zonedDateTime = DateTimeProvider.getInstance().getZonedDateTime();
        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(zonedDateTime.toLocalDateTime().plusSeconds(5))
                .endTime(zonedDateTime.toLocalDateTime().plusSeconds(10)).build();

        MvcResult resultPost = this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()))
                .andReturn();

        String id = JsonPath.read(resultPost.getResponse().getContentAsString(), "$.id");
        this.mockMvc.perform(delete(endpoint + "/" + id))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(scheduledTasksCache.size(), 0);
    }
}

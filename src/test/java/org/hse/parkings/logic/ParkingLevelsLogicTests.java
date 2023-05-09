package org.hse.parkings.logic;

import com.jayway.jsonpath.JsonPath;
import org.hse.parkings.AbstractTest;
import org.hse.parkings.model.Reservation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("ParkingLevel spots map tests")
public class ParkingLevelsLogicTests extends AbstractTest {

    private final String endpointParkingLevels = "/parkingLevels";

    private final String endpointReservations = "/reservations";

    @AfterEach
    void restoreTime() {
        dateTimeProvider.resetClock();
    }

    @Test
    @DisplayName("Get map of parking spots on level")
    public void positive_getMapOfParkingSpotsOnLevel() throws Exception {
        this.mockMvc.perform(get(endpointParkingLevels + "/" + parkingLevelOne.getId() + "/spots"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));

        this.mockMvc.perform(get(endpointParkingLevels + "/" + parkingLevelTwo.getId() + "/spots"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Get reservations map of parking spots on level")
    public void positive_getReservationMapOfParkingSpotsOnLevel() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime startTime = dateTimeProvider.getZonedDateTime().toLocalDateTime().plusHours(2);
        LocalDateTime endTime = dateTimeProvider.getZonedDateTime().toLocalDateTime().plusHours(3);

        this.mockMvc.perform(get(endpointParkingLevels + "/" + parkingLevelOne.getId() + "/freeSpotsInInterval")
                        .param("startTime", String.valueOf(startTime))
                        .param("endTime", String.valueOf(endTime)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));

        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(startTime)
                .endTime(endTime).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carTeslaOfBob.getId())
                .employeeId(employeeBob.getId())
                .parkingSpotId(parkingSpotD.getId())
                .startTime(startTime.plusMinutes(5))
                .endTime(endTime.minusMinutes(5)).build();

        this.mockMvc.perform(post(endpointReservations)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpointReservations)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservationTwo.getCarId().toString()));

        MvcResult resultPost = this.mockMvc.perform(get(endpointParkingLevels + "/" + parkingLevelOne.getId() + "/freeSpotsInInterval")
                        .param("startTime", String.valueOf(startTime))
                        .param("endTime", String.valueOf(endTime)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<String> response = JsonPath.read(resultPost.getResponse().getContentAsString(), "$.[?(@.isFree == false)]");
        Assertions.assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Get reservations map of parking spots on level with possible collision")
    public void positive_getReservationMapOfParkingSpotsOnLevelWithPossibleCollision() throws Exception {
        adjustClockTo(DayOfWeek.MONDAY);
        dateTimeProvider.offsetClock(Duration.ofHours(24 - dateTimeProvider.getZonedDateTime().getHour()));

        LocalDateTime startTime = dateTimeProvider.getZonedDateTime().toLocalDateTime().plusHours(2);
        LocalDateTime endTime = dateTimeProvider.getZonedDateTime().toLocalDateTime().plusHours(3);

        this.mockMvc.perform(get(endpointParkingLevels + "/" + parkingLevelOne.getId() + "/freeSpotsInInterval")
                        .param("startTime", String.valueOf(startTime))
                        .param("endTime", String.valueOf(endTime)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));

        Reservation reservation = Reservation.builder()
                .carId(carSupraOfAlice.getId())
                .employeeId(employeeAlice.getId())
                .parkingSpotId(parkingSpotA.getId())
                .startTime(startTime)
                .endTime(endTime).build();

        Reservation reservationTwo = Reservation.builder()
                .carId(carTeslaOfBob.getId())
                .employeeId(employeeBob.getId())
                .parkingSpotId(parkingSpotB.getId())
                .startTime(startTime.minusHours(1))
                .endTime(startTime).build();

        this.mockMvc.perform(post(endpointReservations)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservation.getCarId().toString()));

        this.mockMvc.perform(post(endpointReservations)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(reservationTwo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId").value(reservationTwo.getCarId().toString()));

        MvcResult resultPost = this.mockMvc.perform(get(endpointParkingLevels + "/" + parkingLevelOne.getId() + "/freeSpotsInInterval")
                        .param("startTime", String.valueOf(startTime))
                        .param("endTime", String.valueOf(endTime)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<String> response = JsonPath.read(resultPost.getResponse().getContentAsString(),
                "$.[?(@.isFree == false)]");
        Assertions.assertEquals(1, response.size());
    }
}

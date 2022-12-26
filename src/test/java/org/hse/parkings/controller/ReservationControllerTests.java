package org.hse.parkings.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hse.parkings.AbstractTest;
import org.hse.parkings.model.Reservation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class ReservationControllerTests extends AbstractTest {

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
    public void positive_getReservationListTest() throws Exception {
        this.mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[" + reservationString + "]")));
    }

    @Test
    public void positive_createReservationTest() throws Exception {
        this.mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservation2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(reservation2String)));
    }

    @Test
    public void positive_getReservationTest() throws Exception {
        this.mockMvc.perform(get("/reservations/" + reservation.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string((containsString(reservationString))));
    }

    @Test
    public void positive_editReservationTest() throws Exception {
//        Reservation reservation2 = new Reservation(
//                car.getId(),
//                employee.getId(),
//                parkingSpot.getId(),
//                LocalDateTime.of(2031, 1, 1, 12, 0, 0),
//                LocalDateTime.of(2031, 1, 1, 13, 0, 0)
//        );
        Reservation reservation2 = Reservation.builder()
                .carId(carSupra.getId())
                .employeeId(employee.getId())
                .parkingSpotId(parkingSpot.getId())
                .startTime(LocalDateTime.of(2031, 1, 1, 12, 0, 0))
                .endTime(LocalDateTime.of(2031, 1, 1, 13, 0, 0)).build();
        reservation2.setId(reservation.getId());
        String reservation2String = jackson.writeValueAsString(reservation2);
        this.mockMvc.perform(put("/reservations/" + reservation.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservation2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(reservation2String)));
    }

    @Test
    public void positive_deleteReservationTest() throws Exception {
        this.mockMvc.perform(delete("/reservations/" + reservation.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void negative_notValidBodyTest() throws Exception {
        this.mockMvc.perform(post("/reservations")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(put("/reservations/" + reservation.getId().toString())
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void negative_contentTypeErrorTest() throws Exception {
        this.mockMvc.perform(post("/reservations")
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
        this.mockMvc.perform(put("/reservations/" + reservation.getId().toString())
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void negative_idNotFoundTest() throws Exception {
        String randomUuid = UUID.randomUUID().toString();
        this.mockMvc.perform(get("/reservations/" + randomUuid))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(put("/reservations/" + randomUuid)
                        .content(reservationString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(delete("/reservations/" + randomUuid))
                .andExpect(status().isOk());
    }
}

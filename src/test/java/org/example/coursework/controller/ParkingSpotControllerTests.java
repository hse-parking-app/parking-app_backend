package org.example.coursework.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.coursework.AbstractTest;
import org.example.coursework.model.ParkingSpot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class ParkingSpotControllerTests extends AbstractTest {

    final ParkingSpot parkingSpot2 = new ParkingSpot(2, true);
    String parkingSpotString;
    String parkingSpot2String;

    {
        try {
            parkingSpotString = jackson.writeValueAsString(parkingSpot);
            parkingSpot2String = jackson.writeValueAsString(parkingSpot2);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void positive_getParkingSpotListTest() throws Exception {
        this.mockMvc.perform(get("/parkingSpots"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[" + parkingSpotString + "]")));
    }

    @Test
    public void positive_createParkingSpotTest() throws Exception {
        this.mockMvc.perform(post("/parkingSpots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parkingSpot2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(parkingSpot2String)));
    }

    @Test
    public void positive_getParkingSpotTest() throws Exception {
        this.mockMvc.perform(get("/parkingSpots/" + parkingSpot.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string((containsString(parkingSpotString))));
    }

    @Test
    public void positive_editParkingSpotTest() throws Exception {
        ParkingSpot parkingSpot3 = parkingSpot2;
        parkingSpot3.setId(parkingSpot.getId());
        String parkingSpot3String = jackson.writeValueAsString(parkingSpot3);
        this.mockMvc.perform(put("/parkingSpots/" + parkingSpot.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parkingSpot3String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(parkingSpot3String)));
    }

    @Test
    public void positive_deleteParkingSpotTest() throws Exception {
        this.mockMvc.perform(delete("/parkingSpots/" + parkingSpot.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void negative_notValidBodyTest() throws Exception {
        this.mockMvc.perform(post("/parkingSpots")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(put("/parkingSpots/" + parkingSpot.getId().toString())
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Content type error")
    public void negative_contentTypeErrorTest() throws Exception {
        this.mockMvc.perform(post("/parkingSpots")
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
        this.mockMvc.perform(put("/parkingSpots/" + parkingSpot.getId().toString())
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void negative_idNotFoundTest() throws Exception {
        String randomUuid = UUID.randomUUID().toString();
        this.mockMvc.perform(get("/parkingSpots/" + randomUuid))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(put("/parkingSpots/" + randomUuid)
                        .content(parkingSpotString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(delete("/parkingSpots/" + randomUuid))
                .andExpect(status().isOk());
    }
}

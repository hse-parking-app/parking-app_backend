package org.hse.parkings.controller;

import org.hse.parkings.AbstractTest;
import org.hse.parkings.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class CarControllerTests extends AbstractTest {

    @Test
    @DisplayName("POST Ok Car")
    public void positive_saveCar() throws Exception {
        Car temp = carSupra;
        UUID uuid = UUID.randomUUID();
        temp.setId(uuid);
        this.mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(uuid.toString()));
    }

    @Test
    @DisplayName("POST Null length & weight Car")
    public void positive_saveNullLengthWeightCar() throws Exception {
        Car temp = carSupra;
        temp.setId(UUID.randomUUID());
        temp.setLengthMeters(null);
        temp.setWeightTons(null);
        this.mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length").doesNotExist())
                .andExpect(jsonPath("$.weight").doesNotExist());
    }

    @Test
    @DisplayName("POST Conflict Car")
    public void negative_saveConflictCar() throws Exception {
        this.mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(carSupra)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    @DisplayName("POST Blank model Car")
    public void negative_saveNoModelCar() throws Exception {
        Car temp = carSupra;
        temp.setModel("");
        this.mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("POST Blank registryNumber Car")
    public void negative_saveNoRegistryNumberCar() throws Exception {
        Car temp = carSupra;
        temp.setRegistryNumber("");
        this.mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsBytes(temp)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400));
    }


    @Test
    @DisplayName("GET Car list")
    public void positive_getCarList() throws Exception {
        this.mockMvc.perform(get("/cars"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET Car")
    public void positive_getCar() throws Exception {
        this.mockMvc.perform(get("/cars/" + carSupra.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(jackson.writeValueAsString(carSupra))));
    }

    @Test
    @DisplayName("PUT Car")
    public void positive_editCar() throws Exception {
        Car temp = carSupra;
        temp.setModel("Ayaya");
        temp.setLengthMeters(10.0);
        String tempCarString = jackson.writeValueAsString(temp);
        this.mockMvc.perform(put("/cars/" + temp.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tempCarString))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(tempCarString)));
    }

    @Test
    @DisplayName("Car Content type error")
    public void negative_contentTypeError() throws Exception {
        this.mockMvc.perform(post("/cars")
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
        this.mockMvc.perform(put("/cars/" + carSupra.getId().toString())
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("DELETE Car")
    public void positive_deleteCar() throws Exception {
        this.mockMvc.perform(delete("/cars/" + carSupra.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Car Not valid body")
    public void negative_notValidBody() throws Exception {
        this.mockMvc.perform(post("/cars")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(put("/cars/" + carSupra.getId().toString())
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET Not existing id Car")
    public void negative_idNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        this.mockMvc.perform(get("/cars/" + uuid))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(put("/cars/" + uuid)
                        .content(jackson.writeValueAsString(carSupra))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(delete("/cars/" + uuid))
                .andExpect(status().isOk());
    }
}

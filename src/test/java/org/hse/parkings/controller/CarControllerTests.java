package org.hse.parkings.controller;

import org.hse.parkings.AbstractTest;
import org.hse.parkings.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("Car controller")
public class CarControllerTests extends AbstractTest {

    private final String endpoint = "/cars";

    @Test
    @DisplayName("POST - Car")
    public void positive_saveCar() throws Exception {
        Car temp = carSupraOfAlice;
        temp.setModel("New Supra");
        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.model").value(temp.getModel()));
    }

    @Test
    @DisplayName("POST - AppUser Car")
    public void positive_postAppUserCar() throws Exception {
        Car temp = carSupraOfAlice;
        temp.setOwnerId(null);
        this.mockMvc.perform(post(endpoint + "/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.model").value(temp.getModel()))
                .andExpect(jsonPath("$.ownerId").value(employeeAlice.getId().toString()));
    }

    @Test
    @DisplayName("POST - Car with null length & weight")
    public void positive_saveNullLengthWeightCar() throws Exception {
        Car temp = carSupraOfAlice;
        temp.setId(UUID.randomUUID());
        temp.setLengthMeters(null);
        temp.setWeightTons(null);
        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length").doesNotExist())
                .andExpect(jsonPath("$.weight").doesNotExist());
    }

    @Test
    @DisplayName("POST - Car without ownerId")
    public void negative_saveCarWithoutOwner() throws Exception {
        Car temp = carSupraOfAlice;
        temp.setOwnerId(null);

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("POST - Car with not existing ownerId")
    public void negative_saveCarWithNotExistingOwner() throws Exception {
        Car temp = carSupraOfAlice;
        temp.setOwnerId(UUID.randomUUID());

        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("POST - Car with blank fields")
    public void negative_saveCarBlankFields() throws Exception {
        Car temp = carSupraOfAlice;
        temp.setModel("");
        temp.setRegistryNumber("");
        this.mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.messages.length()").value(2));
    }

    @Test
    @DisplayName("GET - Car list")
    public void positive_getCarList() throws Exception {
        this.mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].ownerId").exists())
                .andExpect(jsonPath("$.[0].model").exists())
                .andExpect(jsonPath("$.[0].lengthMeters").exists())
                .andExpect(jsonPath("$.[0].weightTons").exists())
                .andExpect(jsonPath("$.[0].registryNumber").exists());
    }

    @Test
    @DisplayName("GET - Car by id")
    public void positive_getCarById() throws Exception {
        this.mockMvc.perform(get(endpoint + "/" + carSupraOfAlice.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(jackson.writeValueAsString(carSupraOfAlice))));
    }

    @Test
    @DisplayName("GET PUT DELETE - Not existing id")
    public void negative_getCarNotExistingId() throws Exception {
        UUID uuid = UUID.randomUUID();
        this.mockMvc.perform(get(endpoint + "/" + uuid))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(put(endpoint + "/" + uuid)
                        .content(jackson.writeValueAsString(carSupraOfAlice))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(delete(endpoint + "/" + uuid))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT - Car")
    public void positive_editCar() throws Exception {
        Car temp = carSupraOfAlice;
        temp.setModel("Ayaya");
        temp.setLengthMeters(10.0);
        String tempCarString = jackson.writeValueAsString(temp);
        this.mockMvc.perform(put(endpoint + "/" + temp.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tempCarString))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(tempCarString)));
    }

    @Test
    @DisplayName("DELETE - Car")
    public void positive_deleteCar() throws Exception {
        this.mockMvc.perform(delete(endpoint + "/" + carSupraOfAlice.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Car Content type error")
    public void negative_contentTypeError() throws Exception {
        this.mockMvc.perform(post(endpoint)
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
        this.mockMvc.perform(put(endpoint + "/" + carSupraOfAlice.getId().toString())
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Car Not valid body")
    public void negative_notValidBody() throws Exception {
        this.mockMvc.perform(post(endpoint)
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(put(endpoint + "/" + carSupraOfAlice.getId().toString())
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

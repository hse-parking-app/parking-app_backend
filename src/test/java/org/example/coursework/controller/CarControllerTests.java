package org.example.coursework.controller;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.coursework.AbstractTest;
import org.example.coursework.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class CarControllerTests extends AbstractTest {

    final Car car2 = new Car("Celica", 4, 2, "785das");
    String carString;
    String car2String;

    {
        try {
            carString = jackson.writeValueAsString(car);
            car2String = jackson.writeValueAsString(car2);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void positive_getCarListTest() throws Exception {
        this.mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[" + carString + "]")));
    }

    @Test
    public void positive_createCarTest() throws Exception {
        this.mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(car2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(car2String)));
    }

    @Test
    public void positive_getCarTest() throws Exception {
        this.mockMvc.perform(get("/cars/" + car.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string((containsString(carString))));
    }

    @Test
    public void positive_editCarTest() throws Exception {
        Car car3 = car2;
        car3.setId(car.getId());
        String car3String = jackson.writeValueAsString(car3);
        this.mockMvc.perform(put("/cars/" + car.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(car3String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(car3String)));
    }
    @Test
    public void positive_deleteCarTest() throws Exception {
        this.mockMvc.perform(delete("/cars/" + car.getId().toString()))
                .andExpect(status().isOk());
    }
    @Test
    public void negative_notValidBodyTest() throws Exception {
        this.mockMvc.perform(post("/cars")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(put("/cars/" + car.getId().toString())
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Content type error")
    public void negative_contentTypeErrorTest() throws Exception {
        this.mockMvc.perform(post("/cars")
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
        this.mockMvc.perform(put("/cars/" + car.getId().toString())
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void negative_idNotFoundTest() throws Exception {
        String randomUuid = UUID.randomUUID().toString();
        this.mockMvc.perform(get("/cars/" + randomUuid))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(put("/cars/" + randomUuid)
                        .content(carString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(delete("/cars/" + randomUuid))
                .andExpect(status().isOk());
    }
}

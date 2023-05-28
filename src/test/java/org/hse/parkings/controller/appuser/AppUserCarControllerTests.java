package org.hse.parkings.controller.appuser;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.hse.parkings.AbstractTest;
import org.hse.parkings.model.Car;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("AppUser Car controller")
public class AppUserCarControllerTests extends AbstractTest {

    @BeforeAll
    void userForTests() throws Exception {
        loginAs(employeeDarlene);
    }

    @Test
    @DisplayName("POST GET PUT DELETE AppUser - Car")
    public void testAppUserCarMethods() throws Exception {
        Car temp = carSupraOfAlice.toBuilder().ownerId(null).build();

        MvcResult resultPost = this.mockMvc.perform(post(carsEndpoint + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.model").value(temp.getModel()))
                .andExpect(jsonPath("$.ownerId").value(employeeDarlene.getId().toString()))
                .andReturn();

        Car postedCar = jackson.readValue(resultPost.getResponse().getContentAsString(), Car.class);
        Car tempTwo = temp.toBuilder().model("NewModel").build();

        this.mockMvc.perform(get(carsEndpoint + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].ownerId").value(employeeDarlene.getId().toString()));

        this.mockMvc.perform(put(carsEndpoint + "/" + postedCar.getId().toString() + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(tempTwo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.model").value(tempTwo.getModel()));

        MvcResult resultGet = this.mockMvc.perform(get(carsEndpoint + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        int numberOfCars = JsonPath.read(resultGet.getResponse().getContentAsString(),
                "$.length()");

        this.mockMvc.perform(delete(carsEndpoint + "/" + postedCar.getId().toString() + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isOk());

        this.mockMvc.perform(get(carsEndpoint + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", Matchers.lessThan(numberOfCars)));
    }

    @Test
    @DisplayName("PUT DELETE AppUser - Foreign car")
    public void negative_appUserUpdateForeignCar() throws Exception {
        Car temp = carSupraOfAlice.toBuilder().model("Oh").build();

        this.mockMvc.perform(put(carsEndpoint + "/" + carSupraOfAlice.getId() + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(delete(carsEndpoint + "/" + carSupraOfAlice.getId() + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isNotFound());
    }
}

package org.hse.parkings.controller.appuser;

import org.hse.parkings.AbstractTest;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.model.jwt.JwtResponse;
import org.junit.jupiter.api.AfterEach;
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
@DisplayName("AppUser Employee controller")
public class AppUserEmployeeControllerTests extends AbstractTest {

    @BeforeAll
    void userForTests() throws Exception {
        loginAs(employeeDarlene);
    }

    @AfterEach
    void tearDownEmployeesAndRestore() {
        deleteEmployees();

        insert(employeeAlice);
        insert(employeeBob);
        insert(employeeDarlene);
    }

    @Test
    @DisplayName("PUT AppUser - Employee")
    public void positive_editAppUserEmployee() throws Exception {
        Employee temp = employeeDarlene.toBuilder().name("Empathy").build();

        MvcResult resultPut = this.mockMvc.perform(put(employeesEndpoint + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").hasJsonPath())
                .andReturn();
        tokens = jackson.readValue(resultPut.getResponse().getContentAsString(), JwtResponse.class);

        this.mockMvc.perform(get(authEndpoint + "/whoami")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(temp.getName()));
    }

    @Test
    @DisplayName("DELETE - AppUser Employee")
    public void positive_deleteAppUserEmployee() throws Exception {
        this.mockMvc.perform(delete(employeesEndpoint + "/employee")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isOk());
    }
}

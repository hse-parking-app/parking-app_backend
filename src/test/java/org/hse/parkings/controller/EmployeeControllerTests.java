package org.hse.parkings.controller;

import org.hse.parkings.AbstractTest;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.model.employee.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("Employee controller")
public class EmployeeControllerTests extends AbstractTest {

    @BeforeAll
    void userForTests() throws Exception {
        loginAs(employeeAlice);
    }

    @AfterEach
    void tearDownEmployeesAndRestore() {
        deleteEmployees();

        insert(employeeAlice);
        insert(employeeBob);
        insert(employeeDarlene);
    }

    @Test
    @DisplayName("POST - Employee")
    public void positive_saveEmployee() throws Exception {
        Employee temp = employeeAlice.toBuilder().email("new@test.t").build();
        this.mockMvc.perform(post(employeesEndpoint)
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(temp.getEmail()));

        loginAs(temp);
    }

    @Test
    @DisplayName("POST - Employee blank fields")
    public void negative_saveEmployeeBlankFields() throws Exception {
        Employee temp = employeeAlice.toBuilder().name("").password("").email("").build();
        this.mockMvc.perform(post(employeesEndpoint)
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.messages.length()").value(3));
    }

    @Test
    @DisplayName("POST - Employee wrong email format")
    public void negative_saveEmployeeWrongEmailFormat() throws Exception {
        Employee temp = employeeAlice.toBuilder().email("Revolution 909").build();
        this.mockMvc.perform(post(employeesEndpoint)
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.messages.length()").value(1))
                .andExpect(jsonPath("$.messages.[0].cause").value("email"));
    }

    @Test
    @DisplayName("GET - Employees list")
    public void positive_getEmployeesList() throws Exception {
        this.mockMvc.perform(get(employeesEndpoint)
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].name").exists())
                .andExpect(jsonPath("$.[0].email").exists())
                .andExpect(jsonPath("$.[0].password").exists());
    }

    @Test
    @DisplayName("GET - Employee by id")
    public void positive_getEmployeeById() throws Exception {
        this.mockMvc.perform(get(employeesEndpoint + "/" + employeeAlice.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeAlice.getId().toString()))
                .andExpect(jsonPath("$.name").value(employeeAlice.getName()))
                .andExpect(jsonPath("$.email").value(employeeAlice.getEmail()))
                .andExpect(jsonPath("$.roles.length()").value(employeeAlice.getRoles().size()));
    }

    @Test
    @DisplayName("GET PUT DELETE - Not existing id Employee")
    public void negative_getEmployeeNotExistingId() throws Exception {
        UUID uuid = UUID.randomUUID();
        this.mockMvc.perform(get(employeesEndpoint + "/" + uuid)
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(put(employeesEndpoint + "/" + uuid)
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .content(jackson.writeValueAsString(employeeAlice))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(delete(employeesEndpoint + "/" + uuid)
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT - Employee")
    public void positive_editEmployee() throws Exception {
        Employee temp = employeeAlice.toBuilder().name("Alisa").roles(Set.of(Role.APP_USER, Role.ADMIN)).build();
        String tempEmployeeString = jackson.writeValueAsString(temp);
        this.mockMvc.perform(put(employeesEndpoint + "/" + temp.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tempEmployeeString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(temp.getName()))
                .andExpect(jsonPath("$.roles.length()").value(temp.getRoles().size()));
    }

    @Test
    @DisplayName("DELETE - Employee")
    public void positive_deleteEmployee() throws Exception {
        this.mockMvc.perform(delete(employeesEndpoint + "/" + employeeAlice.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isOk());
        this.mockMvc.perform(get(employeesEndpoint + "/" + employeeAlice.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Employee Content type error")
    public void negative_contentTypeError() throws Exception {
        this.mockMvc.perform(post(employeesEndpoint)
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
        this.mockMvc.perform(put(employeesEndpoint + "/" + employeeAlice.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Employee Not valid body")
    public void negative_notValidBody() throws Exception {
        this.mockMvc.perform(post(employeesEndpoint)
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(put(employeesEndpoint + "/" + employeeAlice.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

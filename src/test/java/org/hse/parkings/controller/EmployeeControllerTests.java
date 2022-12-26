package org.hse.parkings.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hse.parkings.AbstractTest;
import org.hse.parkings.model.Employee;
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
public class EmployeeControllerTests extends AbstractTest {

    //    final Employee employee2 = new Employee("Boba");
    final Employee employee2 = Employee.builder()
            .name("Boba").build();
    String employeeString;
    String employee2String;

    {
        try {
            employeeString = jackson.writeValueAsString(employee);
            employee2String = jackson.writeValueAsString(employee2);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void positive_getEmployeeListTest() throws Exception {
        this.mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[" + employeeString + "]")));
    }

    @Test
    public void positive_createEmployeeTest() throws Exception {
        this.mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employee2String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(employee2String)));
    }

    @Test
    public void positive_getEmployeeTest() throws Exception {
        this.mockMvc.perform(get("/employees/" + employee.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string((containsString(employeeString))));
    }

    @Test
    public void positive_editEmployeeTest() throws Exception {
        Employee employee3 = employee2;
        employee3.setId(employee.getId());
        String employee3String = jackson.writeValueAsString(employee3);
        this.mockMvc.perform(put("/employees/" + employee.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employee3String))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(employee3String)));
    }

    @Test
    public void positive_deleteEmployeeTest() throws Exception {
        this.mockMvc.perform(delete("/employees/" + employee.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void negative_notValidBodyTest() throws Exception {
        this.mockMvc.perform(post("/employees")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(put("/employees/" + employee.getId().toString())
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Content type error")
    public void negative_contentTypeErrorTest() throws Exception {
        this.mockMvc.perform(post("/employees")
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
        this.mockMvc.perform(put("/employees/" + employee.getId().toString())
                        .content(""))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void negative_idNotFoundTest() throws Exception {
        String randomUuid = UUID.randomUUID().toString();
        this.mockMvc.perform(get("/employees/" + randomUuid))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(put("/employees/" + randomUuid)
                        .content(employeeString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(delete("/employees/" + randomUuid))
                .andExpect(status().isOk());
    }
}

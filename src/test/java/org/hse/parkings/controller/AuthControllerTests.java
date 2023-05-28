package org.hse.parkings.controller;

import com.jayway.jsonpath.JsonPath;
import org.hse.parkings.AbstractTest;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.model.jwt.JwtResponse;
import org.hse.parkings.model.jwt.RefreshJwtRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("Auth controller")
public class AuthControllerTests extends AbstractTest {

    @Test
    @DisplayName("Login")
    public void positive_loginAsEmployee() throws Exception {
        this.mockMvc.perform(post(authEndpoint + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(employeeDarlene)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Login with not existing email")
    public void negative_loginWithNotExistingEmail() throws Exception {
        Employee temp = employeeDarlene.toBuilder().email("f@hdd.dd").build();
        this.mockMvc.perform(post(authEndpoint + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Login with wrong password")
    public void negative_loginWithWrongPassword() throws Exception {
        Employee temp = employeeDarlene.toBuilder().password("wegoinghooome").build();
        this.mockMvc.perform(post(authEndpoint + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Sign up")
    public void positive_signUpNewEmployee() throws Exception {
        Employee temp = Employee.builder()
                .name("Dummy")
                .email("d@df.d")
                .password("321").build();

        MvcResult resultPost = mockMvc.perform(post(authEndpoint + "/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        tokens = jackson.readValue(resultPost.getResponse().getContentAsString(), JwtResponse.class);

        mockMvc.perform(get("/building")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Sign up with already existing email")
    public void negative_signUpWithAlreadyExistingEmail() throws Exception {
        Employee temp = Employee.builder()
                .name("Dummy")
                .email(employeeDarlene.getEmail())
                .password("444").build();

        mockMvc.perform(post(authEndpoint + "/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(temp)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Update access token")
    public void positive_updateAccessToken() throws Exception {
        loginAs(employeeDarlene);

        String saveOldAccessToken = tokens.getAccessToken();

        RefreshJwtRequest request = new RefreshJwtRequest();
        request.setRefreshToken(tokens.getRefreshToken());
        MvcResult resultPost = mockMvc.perform(post(authEndpoint + "/update/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JwtResponse temp = jackson.readValue(resultPost.getResponse().getContentAsString(), JwtResponse.class);
        Assertions.assertNotNull(temp.getAccessToken());
        Assertions.assertNull(temp.getRefreshToken());

        tokens.setAccessToken(JsonPath.read(resultPost.getResponse().getContentAsString(), "$.accessToken"));

        mockMvc.perform(get("/building")
                        .header(HttpHeaders.AUTHORIZATION, bearer + saveOldAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/building")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Update access token with foreign refresh token")
    public void negative_wrongJwtOnUpdateAccessToken() throws Exception {
        RefreshJwtRequest request = new RefreshJwtRequest();
        request.setRefreshToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsQGwubCIsImV4cCI6MTY5MDAzMTcyNH0.CzV_o_iHdynBIxJs_h49LnkY6LrDABChRTf_RfHV4PSBz04l9ikHQ87QtuTsT2PKBC4se-ycnbHMv5SrFf714A");
        mockMvc.perform(post(authEndpoint + "/update/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Update access token with empty refresh token")
    public void negative_emptyJwtOnUpdateAccessToken() throws Exception {
        RefreshJwtRequest request = new RefreshJwtRequest();
        request.setRefreshToken("");
        mockMvc.perform(post(authEndpoint + "/update/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Update refresh token with foreign refresh token")
    public void negative_wrongJwtOnUpdateRefreshToken() throws Exception {
        loginAs(employeeDarlene);

        RefreshJwtRequest request = new RefreshJwtRequest();
        request.setRefreshToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsQGwubCIsImV4cCI6MTY5MDAzMTcyNH0.CzV_o_iHdynBIxJs_h49LnkY6LrDABChRTf_RfHV4PSBz04l9ikHQ87QtuTsT2PKBC4se-ycnbHMv5SrFf714A");
        mockMvc.perform(post(authEndpoint + "/update/refresh")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Update refresh token with empty refresh token")
    public void negative_emptyJwtOnUpdateRefreshToken() throws Exception {
        loginAs(employeeDarlene);

        RefreshJwtRequest request = new RefreshJwtRequest();
        request.setRefreshToken("");
        mockMvc.perform(post(authEndpoint + "/update/refresh")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Update refresh token")
    public void positive_updateRefreshToken() throws Exception {
        loginAs(employeeDarlene);

        RefreshJwtRequest request = new RefreshJwtRequest();
        request.setRefreshToken(tokens.getRefreshToken());
        MvcResult resultPost = mockMvc.perform(post(authEndpoint + "/update/refresh")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        tokens = jackson.readValue(resultPost.getResponse().getContentAsString(), JwtResponse.class);
        Assertions.assertNotNull(tokens.getAccessToken());
        Assertions.assertNotNull(tokens.getRefreshToken());

        String saveOldAccessToken = tokens.getAccessToken();

        request.setRefreshToken(tokens.getRefreshToken());
        resultPost = mockMvc.perform(post(authEndpoint + "/update/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JwtResponse temp = jackson.readValue(resultPost.getResponse().getContentAsString(), JwtResponse.class);
        Assertions.assertNotNull(temp.getAccessToken());
        Assertions.assertNull(temp.getRefreshToken());

        tokens.setAccessToken(JsonPath.read(resultPost.getResponse().getContentAsString(), "$.accessToken"));

        mockMvc.perform(get("/building")
                        .header(HttpHeaders.AUTHORIZATION, bearer + saveOldAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/building")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Who am I")
    public void positive_whoAmI() throws Exception {
        loginAs(employeeDarlene);

        mockMvc.perform(get(authEndpoint + "/whoami")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(employeeDarlene.getEmail()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Not authenticated")
    public void negative_notAuthenticated() throws Exception {
        mockMvc.perform(get(authEndpoint + "/whoami")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Authenticated but access is denied")
    public void negative_authenticatedDeniedAccess() throws Exception {
        loginAs(employeeDarlene);

        mockMvc.perform(get("/employees")
                        .header(HttpHeaders.AUTHORIZATION, bearer + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Passing random JWT with method")
    public void negative_passingRandomJwt() throws Exception {
        mockMvc.perform(get(authEndpoint + "/whoami")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + "giveemlove")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}

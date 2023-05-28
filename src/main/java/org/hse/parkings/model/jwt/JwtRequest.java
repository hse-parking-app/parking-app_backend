package org.hse.parkings.model.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class JwtRequest {

    @NotNull(message = "Email in token request is required parameter")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Password in token request is required parameter")
    @NotBlank(message = "Password cannot be blank")
    private String password;
}

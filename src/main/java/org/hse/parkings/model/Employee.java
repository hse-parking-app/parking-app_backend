package org.hse.parkings.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class Employee {

    @Builder.Default
    UUID id = UUID.randomUUID();

    @NotNull(message = "Employee name is required parameter")
    @NotBlank(message = "Name cannot be blank")
    String name;

    @NotNull(message = "Employee email is required parameter")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    String email;

    @NotNull(message = "Employee password is required parameter")
    @NotBlank(message = "Password cannot be blank")
    String password;
}

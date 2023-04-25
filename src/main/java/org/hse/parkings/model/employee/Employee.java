package org.hse.parkings.model.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
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

    @NotNull(message = "Employee roles is required parameter")
    @NotEmpty(message = "Employee cannot have no roles")
    Set<Role> roles;
}

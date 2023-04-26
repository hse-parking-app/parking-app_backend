package org.hse.parkings.model.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hse.parkings.validate.groups.AppUserEmployee;
import org.hse.parkings.validate.groups.DefaultEmployee;

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

    @NotNull(message = "Employee name is required parameter", groups = {AppUserEmployee.class, DefaultEmployee.class})
    @NotBlank(message = "Name cannot be blank", groups = {AppUserEmployee.class, DefaultEmployee.class})
    String name;

    @NotNull(message = "Employee email is required parameter", groups = {AppUserEmployee.class, DefaultEmployee.class})
    @NotBlank(message = "Email cannot be blank", groups = {AppUserEmployee.class, DefaultEmployee.class})
    @Email(message = "Email must be valid", groups = {AppUserEmployee.class, DefaultEmployee.class})
    String email;

    @NotNull(message = "Employee password is required parameter", groups = {AppUserEmployee.class, DefaultEmployee.class})
    @NotBlank(message = "Password cannot be blank", groups = {AppUserEmployee.class, DefaultEmployee.class})
    String password;

    @NotNull(message = "Employee roles is required parameter", groups = {DefaultEmployee.class})
    @NotEmpty(message = "Employee cannot have no roles", groups = {DefaultEmployee.class})
    Set<Role> roles;
}

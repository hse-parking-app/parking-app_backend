package org.hse.parkings.controller;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.model.jwt.JwtResponse;
import org.hse.parkings.service.EmployeeService;
import org.hse.parkings.validate.groups.employee.AppUserEmployee;
import org.hse.parkings.validate.groups.employee.DefaultEmployee;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Set<Employee> getAll() {
        return service.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Employee create(@Validated(DefaultEmployee.class) @RequestBody Employee employee) {
        return service.save(employee);
    }

    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Employee get(@PathVariable UUID employeeId) {
        return service.find(employeeId);
    }

    @PutMapping(value = "/{employeeId}", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Employee edit(@PathVariable UUID employeeId, @Validated(DefaultEmployee.class) @RequestBody Employee employee) {
        employee.setId(employeeId);
        return service.update(employee);
    }

    @DeleteMapping("/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void delete(@PathVariable UUID employeeId) {
        service.delete(employeeId);
    }

    @PutMapping("/employee")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    JwtResponse editEmployee(@Validated(AppUserEmployee.class) @RequestBody Employee employee) {
        return service.updateEmployee(employee);
    }

    @DeleteMapping("/employee")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    void deleteEmployee() {
        service.deleteEmployee();
    }
}

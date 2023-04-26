package org.hse.parkings.controller;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.service.EmployeeService;
import org.hse.parkings.validate.groups.DefaultEmployee;
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

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void deleteAll() {
        service.deleteAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Employee get(@PathVariable UUID id) {
        return service.find(id);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Employee edit(@PathVariable UUID id, @RequestBody Employee employee) {
        employee.setId(id);
        return service.update(employee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}

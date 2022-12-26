package org.hse.parkings.controller;

import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.Employee;
import org.hse.parkings.service.EmployeeService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    Set<Employee> getAll() throws NotFoundException {
        return service.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    Employee create(@Valid @RequestBody Employee employee) {
        return service.save(employee);
    }

    @DeleteMapping
    @Secured("ROLE_ADMIN")
    void deleteAll() {
        service.deleteAll();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    Employee get(@PathVariable UUID id) {
        return service.find(id);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    Employee edit(@PathVariable UUID id, @RequestBody Employee employee) {
        employee.setId(id);
        return service.update(employee);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}

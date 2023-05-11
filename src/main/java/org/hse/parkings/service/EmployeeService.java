package org.hse.parkings.service;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.dao.EmployeeRepository;
import org.hse.parkings.exception.AlreadyExistsException;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.model.employee.Role;
import org.hse.parkings.model.jwt.JwtAuthentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.employeeCache;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;

    private final PasswordEncoder encoder;

    private final AuthService authService;

    public Employee save(Employee employee) throws AlreadyExistsException {
        Employee toSave = Employee.builder()
                .name(employee.getName())
                .email(employee.getEmail())
                .password(encoder.encode(employee.getPassword()))
                .roles(employee.getRoles()).build();
        repository.findByEmail(toSave.getEmail()).ifPresent(s -> {
            throw new AlreadyExistsException("Employee with email = " + s.getEmail() + " already exists");
        });
        repository.save(toSave);
        employeeCache.remove(toSave.getId());
        return find(toSave.getId());
    }

    public Employee update(Employee employee) {
        employee.setPassword(encoder.encode(employee.getPassword()));
        repository.update(employee);
        employeeCache.remove(employee.getId());
        return find(employee.getId());
    }

    public Employee updateEmployee(Employee employee) throws NotFoundException {
        JwtAuthentication authInfo = authService.getAuthInfo();

        if (!employee.getId().equals(authInfo.getId())) {
            throw new NotFoundException("Employee with id = " + employee.getId() + " not found");
        }
        employee.setRoles(Collections.singleton(Role.APP_USER));

        return update(employee);
    }

    public void delete(UUID id) {
        repository.delete(id);
        employeeCache.remove(id);
    }

    public void deleteAll() {
        repository.deleteAll();
        employeeCache.clear();
    }

    public void deleteEmployee() {
        JwtAuthentication authInfo = authService.getAuthInfo();

        delete(authInfo.getId());
    }

    public Employee find(UUID id) throws NotFoundException {
        if (employeeCache.containsKey(id)) {
            return employeeCache.get(id);
        }
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with id = " + id + " not found"));
        employeeCache.put(id, employee);
        return employee;
    }

    public Employee findByEmail(String email) throws NotFoundException {
        Employee employee = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee with email = " + email + " not found"));
        employeeCache.put(employee.getId(), employee);
        return employee;
    }

    public Set<Employee> findAll() {
        return repository.findAll();
    }

    public String findRefreshToken(String email) {
        return repository.getRefreshToken(email);
    }

    public void saveRefreshToken(String email, String refreshToken) {
        repository.putRefreshToken(email, refreshToken);
    }

    public void deleteAllRefreshTokens() {
        repository.deleteAllRefreshTokens();
    }
}

package org.hse.parkings.service;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.dao.EmployeeRepository;
import org.hse.parkings.exception.AlreadyExistsException;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.employee.Employee;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.employeeCache;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;

    public Employee save(Employee employee) throws AlreadyExistsException {
        Employee toSave = Employee.builder()
                .name(employee.getName())
                .email(employee.getEmail())
                .password(employee.getPassword())
                .roles(employee.getRoles()).build();
        repository.findByEmail(toSave.getEmail()).ifPresent(s -> {
            throw new AlreadyExistsException("Employee with email = " + s.getEmail() + " already exists");
        });
        repository.save(toSave);
        employeeCache.remove(toSave.getId());
        return find(toSave.getId());
    }

    public Employee update(Employee employee) {
        repository.update(employee);
        employeeCache.remove(employee.getId());
        return find(employee.getId());
    }

    public void delete(UUID id) {
        repository.delete(id);
        employeeCache.remove(id);
    }

    public void deleteAll() {
        repository.deleteAll();
        employeeCache.clear();
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

    public void deleteAllRefreshKeys() {
        repository.deleteAllRefreshKeys();
    }
}

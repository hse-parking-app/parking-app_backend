package org.hse.parkings.service;

import org.hse.parkings.dao.EmployeeRepository;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.Employee;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.employeeCache;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee save(Employee employee) {
        Employee toSave = Employee.builder()
                .name(employee.getName()).build();
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
        } else {
            Employee employee = repository
                    .find(id)
                    .orElseThrow(() -> new NotFoundException("Employee with id = " + id + " not found"));
            employeeCache.put(id, employee);
            return employee;
        }
    }

    public Set<Employee> findAll() {
        return repository.findAll();
    }
}

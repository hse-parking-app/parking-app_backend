package org.example.coursework.service;

import java.util.Set;
import java.util.UUID;

import org.example.coursework.dao.EmployeeRepository;
import org.example.coursework.error.NotFoundException;
import org.example.coursework.model.Employee;
import org.springframework.stereotype.Service;

import static org.example.coursework.utils.Cache.employeeCache;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee save(Employee employee) {
        repository.save(employee);
        employeeCache.remove(employee.getId());
        return find(employee.getId());
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

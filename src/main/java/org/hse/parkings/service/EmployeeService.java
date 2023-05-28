package org.hse.parkings.service;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.dao.EmployeeRepository;
import org.hse.parkings.exception.AlreadyExistsException;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.Reservation;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.model.employee.Role;
import org.hse.parkings.model.jwt.JwtAuthentication;
import org.hse.parkings.model.jwt.JwtRequest;
import org.hse.parkings.model.jwt.JwtResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.hse.parkings.utils.Cache.employeeCache;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class EmployeeService {

    private final EmployeeRepository repository;

    private final PasswordEncoder encoder;

    private final AuthService authService;

    @Lazy
    private final ReservationService reservationService;

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
        Employee toUpdate = employee.toBuilder().password(encoder.encode(employee.getPassword())).build();
        repository.update(toUpdate);
        employeeCache.remove(toUpdate.getId());
        return find(toUpdate.getId());
    }

    public JwtResponse updateEmployee(Employee employee) throws NotFoundException {
        JwtAuthentication authInfo = authService.getAuthInfo();

        employee.setId(authInfo.getId());
        employee.setRoles(Collections.singleton(Role.APP_USER));

        update(employee);

        return authService.login(new JwtRequest(employee.getEmail(), employee.getPassword()));
    }

    public void delete(UUID id) {
        repository.delete(id);

        Set<Reservation> reservations = reservationService.findEmployeeReservations(id);
        reservations.forEach(reservation -> reservationService.deleteEmployeeReservation(reservation.getId()));

        employeeCache.remove(id);
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

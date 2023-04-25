package org.hse.parkings.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.hse.parkings.dao.EmployeeRepository;
import org.hse.parkings.exception.AuthException;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.model.employee.Role;
import org.hse.parkings.model.jwt.JwtAuthentication;
import org.hse.parkings.model.jwt.JwtRequest;
import org.hse.parkings.model.jwt.JwtResponse;
import org.hse.parkings.security.JwtProvider;
import org.hse.parkings.utils.Log;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;

    private final EmployeeService employeeService;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder encoder;

    public JwtResponse login(JwtRequest authRequest) throws AuthException {
        Employee employee = employeeRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new AuthException("Wrong email or password"));
        if (encoder.matches(authRequest.getPassword(), employee.getPassword())) {
            String accessToken = jwtProvider.generateAccessToken(employee);
            String refreshToken = jwtProvider.generateRefreshToken(employee);
            employeeRepository.putRefreshToken(employee.getEmail(), refreshToken);

            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Wrong email or password");
        }
    }

    public JwtResponse signUp(Employee employee) throws AuthException {
        employee.setPassword(encoder.encode(employee.getPassword()));
        employee.setRoles(Collections.singleton(Role.APP_USER));
        employeeService.save(employee);
        String accessToken = jwtProvider.generateAccessToken(employee);
        String refreshToken = jwtProvider.generateRefreshToken(employee);
        employeeRepository.putRefreshToken(employee.getEmail(), refreshToken);

        return new JwtResponse(accessToken, refreshToken);
    }

    public JwtResponse getAccessToken(String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            String saveRefreshToken = employeeRepository.getRefreshToken(email);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                Employee employee = employeeRepository.findByEmail(email)
                        .orElseThrow(() -> new AuthException("Employee no longer exists"));
                String accessToken = jwtProvider.generateAccessToken(employee);

                return new JwtResponse(accessToken, null);
            } else {
                throw new AuthException("Refresh token is valid but is unknown");
            }
        }

        throw new AuthException("Invalid refresh token");
    }

    public JwtResponse refresh(String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            String saveRefreshToken = employeeRepository.getRefreshToken(email);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                Employee employee = employeeRepository.findByEmail(email)
                        .orElseThrow(() -> new AuthException("Wrong email or password"));
                String accessToken = jwtProvider.generateAccessToken(employee);
                String newRefreshToken = jwtProvider.generateRefreshToken(employee);
                employeeRepository.putRefreshToken(employee.getEmail(), newRefreshToken);

                return new JwtResponse(accessToken, newRefreshToken);
            }
        }

        throw new AuthException("Invalid JWT token");
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logoutAll() {
        employeeRepository.deleteAllRefreshKeys();

        Log.logger.info("Refresh keys cleared");
    }
}

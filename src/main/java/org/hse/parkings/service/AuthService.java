package org.hse.parkings.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hse.parkings.exception.AuthException;
import org.hse.parkings.exception.NotFoundException;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.model.employee.Role;
import org.hse.parkings.model.jwt.JwtAuthentication;
import org.hse.parkings.model.jwt.JwtRequest;
import org.hse.parkings.model.jwt.JwtResponse;
import org.hse.parkings.security.JwtProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
public class AuthService {

    private final PasswordEncoder encoder;

    @Lazy
    private final EmployeeService employeeService;

    private final JwtProvider jwtProvider;

    public JwtResponse login(JwtRequest authRequest) throws AuthException {
        Employee employee;
        try {
            employee = employeeService.findByEmail(authRequest.getEmail());
        } catch (NotFoundException e) {
            throw new AuthException("Wrong email or password");
        }
        if (encoder.matches(authRequest.getPassword(), employee.getPassword())) {
            String accessToken = jwtProvider.generateAccessToken(employee);
            String refreshToken = jwtProvider.generateRefreshToken(employee);
            employeeService.saveRefreshToken(employee.getEmail(), refreshToken);

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
        employeeService.saveRefreshToken(employee.getEmail(), refreshToken);

        return new JwtResponse(accessToken, refreshToken);
    }

    public JwtResponse getAccessToken(String refreshToken) throws AuthException {
        if (refreshToken != null && jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            String saveRefreshToken = employeeService.findRefreshToken(email);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                Employee employee = employeeService.findByEmail(email);
                String accessToken = jwtProvider.generateAccessToken(employee);

                return new JwtResponse(accessToken, null);
            } else {
                throw new AuthException("Refresh token is valid but is unknown");
            }
        }

        throw new AuthException("Invalid refresh token");
    }

    public JwtResponse getRefreshToken(String refreshToken) throws AuthException {
        if (refreshToken != null && jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            String saveRefreshToken = employeeService.findRefreshToken(email);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                Employee employee = employeeService.findByEmail(email);
                String accessToken = jwtProvider.generateAccessToken(employee);
                String newRefreshToken = jwtProvider.generateRefreshToken(employee);
                employeeService.saveRefreshToken(employee.getEmail(), newRefreshToken);

                return new JwtResponse(accessToken, newRefreshToken);
            } else {
                throw new AuthException("Refresh token is valid but is unknown");
            }
        }

        throw new AuthException("Invalid JWT token");
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logoutAll() {
        employeeService.deleteAllRefreshTokens();

        log.info("Refresh tokens cleared");
    }
}

package org.hse.parkings.controller;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.model.jwt.JwtAuthentication;
import org.hse.parkings.model.jwt.JwtRequest;
import org.hse.parkings.model.jwt.JwtResponse;
import org.hse.parkings.model.jwt.RefreshJwtRequest;
import org.hse.parkings.service.AuthService;
import org.hse.parkings.validate.groups.AppUserEmployee;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody JwtRequest authRequest) {
        return authService.login(authRequest);
    }

    @PostMapping("/signUp")
    public JwtResponse signUp(@Validated({AppUserEmployee.class}) @RequestBody Employee employee) {
        return authService.signUp(employee);
    }

    @PostMapping("/update/access")
    public JwtResponse getNewAccessToken(@Valid @RequestBody RefreshJwtRequest request) {
        return authService.getAccessToken(request.getRefreshToken());
    }

    @PostMapping("/update/refresh")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    public JwtResponse getNewRefreshToken(@Valid @RequestBody RefreshJwtRequest request) {
        return authService.refresh(request.getRefreshToken());
    }

    @GetMapping("/whoami")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    public JwtAuthentication getAuthInfo() {
        return authService.getAuthInfo();
    }
}

package org.hse.parkings.controller;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.Reservation;
import org.hse.parkings.model.jwt.JwtAuthentication;
import org.hse.parkings.service.AuthService;
import org.hse.parkings.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService service;

    private final AuthService authService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Set<Reservation> getAll() {
        return service.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Reservation create(@Valid @RequestBody Reservation reservation) {
        return service.save(reservation);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void deleteAll() {
        service.deleteAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Reservation get(@PathVariable UUID id) {
        return service.find(id);
    }

    @GetMapping("/employee")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Set<Reservation> getEmployeeReservations() {
        JwtAuthentication authInfo = authService.getAuthInfo();
        return service.findEmployeeReservations(authInfo.getId());
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Reservation extendReservation(@PathVariable UUID id,
                                  @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return service.extendReservation(id, endTime);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}

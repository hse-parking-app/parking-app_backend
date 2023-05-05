package org.hse.parkings.controller;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.Reservation;
import org.hse.parkings.service.AuthService;
import org.hse.parkings.service.ReservationService;
import org.hse.parkings.validate.groups.reservation.AppUserReservation;
import org.hse.parkings.validate.groups.reservation.DefaultReservation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    private final AuthService authService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Set<Reservation> getAll() {
        return reservationService.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Reservation create(@Validated(DefaultReservation.class) @RequestBody Reservation reservation) {
        return reservationService.save(reservation);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void deleteAll() {
        reservationService.deleteAll();
    }

    @GetMapping("/{reservationId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Reservation get(@PathVariable UUID reservationId) {
        return reservationService.find(reservationId);
    }

    @PutMapping(value = "/{reservationId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Reservation extendReservation(@PathVariable UUID reservationId,
                                  @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return reservationService.extendReservation(reservationId, endTime);
    }

    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void delete(@PathVariable UUID reservationId) {
        reservationService.delete(reservationId);
    }

    @GetMapping("/employee")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Set<Reservation> getEmployeeReservations() {
        return reservationService.findEmployeeReservations(authService.getAuthInfo().getId());
    }

    @PostMapping(value = "/employee", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Reservation createEmployeeReservation(@Validated(AppUserReservation.class) @RequestBody Reservation reservation) {
        return reservationService.saveEmployeeReservation(reservation);
    }

    @PutMapping(value = "/{reservationId}/employee")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    Reservation extendEmployeeReservation(@PathVariable UUID reservationId,
                                          @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return reservationService.extendEmployeeReservation(reservationId, endTime);
    }

    @DeleteMapping("/{reservationId}/employee")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
    void deleteEmployeeReservation(@PathVariable UUID reservationId) {
        reservationService.deleteEmployeeReservation(reservationId);
    }
}

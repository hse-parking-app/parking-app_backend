package org.hse.parkings.controller;

import org.hse.parkings.model.Reservation;
import org.hse.parkings.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    Set<Reservation> getAll() {
        return service.findAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    Reservation create(@Valid @RequestBody Reservation reservation) {
        return service.save(reservation);
    }

    @DeleteMapping
    @Secured("ROLE_ADMIN")
    void deleteAll() {
        service.deleteAll();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    Reservation get(@PathVariable UUID id) {
        return service.find(id);
    }

    @PutMapping(value = "/{id}")
    @Secured("ROLE_ADMIN")
    Reservation extendReservation(@PathVariable UUID id,
                                  @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return service.extendReservation(id, endTime);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}

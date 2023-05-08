package org.hse.parkings.controller;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.utils.DateTimeProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/time")
@RequiredArgsConstructor
public class TimeController {

    private final DateTimeProvider dateTimeProvider;

    @GetMapping("/current")
    ZonedDateTime getCurrentTime() {
        return dateTimeProvider.getZonedDateTime();
    }
}

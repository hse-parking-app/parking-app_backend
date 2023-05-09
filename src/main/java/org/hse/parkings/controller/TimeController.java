package org.hse.parkings.controller;

import lombok.RequiredArgsConstructor;
import org.hse.parkings.utils.DateTimeProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/time")
@RequiredArgsConstructor
public class TimeController {

    private final DateTimeProvider dateTimeProvider;

    @GetMapping("/current")
    String getCurrentTime() {
        return dateTimeProvider.getZonedDateTime().toString();
    }
}

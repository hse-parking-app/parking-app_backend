package org.hse.parkings.utils;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;

@Component
@NoArgsConstructor
public class DateTimeProvider {

    private final Clock defaultClock = Clock.systemUTC();

    private Clock clock = defaultClock;

    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.now(clock);
    }

    public Clock getClock() {
        return this.clock;
    }

    public void resetClock() {
        this.clock = this.defaultClock;
    }

    public void offsetClock(Duration duration) {
        this.clock = Clock.offset(this.clock, duration);
    }
}

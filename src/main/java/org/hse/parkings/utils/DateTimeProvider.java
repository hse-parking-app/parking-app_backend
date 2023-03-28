package org.hse.parkings.utils;

import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;

public enum DateTimeProvider {
    INSTANCE;

    private final Clock defaultClock = Clock.systemDefaultZone();
    private Clock clock = defaultClock;

    public static DateTimeProvider getInstance() {
        return INSTANCE;
    }

    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.now(clock);
    }

    public Clock getClock() {
        return this.clock;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public void resetClock() {
        this.clock = this.defaultClock;
    }

    public void offsetClock(Duration duration) {
        this.clock = Clock.offset(this.clock, duration);
    }
}

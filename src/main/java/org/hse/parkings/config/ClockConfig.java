package org.hse.parkings.config;

import org.hse.parkings.utils.DateTimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class ClockConfig {

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return DateTimeProvider.getInstance();
    }

    @Bean
    public Clock getClock() {
        return DateTimeProvider.getInstance().getClock();
    }
}

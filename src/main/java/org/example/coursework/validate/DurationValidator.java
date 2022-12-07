/*
 * Copyright (c) 2021-2021.
 * Written by Maksim Stepanenko <stepanenko-qa@yandex.ru>
 */

package org.example.coursework.validate;

import java.time.Duration;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.example.coursework.model.Reservation;
import org.springframework.stereotype.Component;

@Component
public class DurationValidator implements ConstraintValidator<DurationIsLess24Hours, Reservation> {

    @Override
    public boolean isValid(Reservation reservation, ConstraintValidatorContext constraintValidatorContext) {
        Duration duration = Duration.between(reservation.getStartTime(), reservation.getEndTime());
        return duration.toHours() <= 24L && !duration.isNegative();
    }
}

/*
 * Copyright (c) 2021-2021.
 * Written by Maksim Stepanenko <stepanenko-qa@yandex.ru>
 */

package org.example.coursework.validate;

import java.time.DayOfWeek;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.example.coursework.model.Reservation;
import org.springframework.stereotype.Component;

@Component
public class WeekendValidator implements ConstraintValidator<NotWeekends, Reservation> {

    @Override
    public boolean isValid(Reservation reservation, ConstraintValidatorContext constraintValidatorContext) {
        DayOfWeek startTimeDayOfWeek = reservation.getStartTime().getDayOfWeek();
        DayOfWeek endTimeDayOfWeek = reservation.getEndTime().getDayOfWeek();
        return startTimeDayOfWeek.getValue() < 6 && endTimeDayOfWeek.getValue() < 6;
    }
}

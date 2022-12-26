package org.hse.parkings.validate;

import org.hse.parkings.model.Reservation;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.DayOfWeek;

@Component
public class WeekendValidator implements ConstraintValidator<NotWeekends, Reservation> {

    @Override
    public boolean isValid(Reservation reservation, ConstraintValidatorContext constraintValidatorContext) {
        DayOfWeek startTimeDayOfWeek = reservation.getStartTime().getDayOfWeek();
        DayOfWeek endTimeDayOfWeek = reservation.getEndTime().getDayOfWeek();
        return startTimeDayOfWeek.getValue() < 6 && endTimeDayOfWeek.getValue() < 6;
    }
}

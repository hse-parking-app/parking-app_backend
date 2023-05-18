package org.hse.parkings.validate;

import org.hse.parkings.model.Reservation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.temporal.ChronoUnit;

public class MaximumReservationRangeValidator implements ConstraintValidator<MaximumReservationRange, Reservation> {

    String message;

    @Override
    public void initialize(MaximumReservationRange constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Reservation reservation, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("startTime")
                .addConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("endTime")
                .addConstraintViolation();

        return ChronoUnit.DAYS.between(reservation.getStartTime(), reservation.getEndTime()) < 1;
    }
}

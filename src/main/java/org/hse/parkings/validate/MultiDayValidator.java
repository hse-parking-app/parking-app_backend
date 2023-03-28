package org.hse.parkings.validate;

import org.hse.parkings.model.Reservation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MultiDayValidator implements ConstraintValidator<NotMultiDayReservation, Reservation> {

    String message;

    @Override
    public void initialize(NotMultiDayReservation constraintAnnotation) {
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

        return reservation.getStartTime().toLocalDate().isEqual(reservation.getEndTime().toLocalDate());
    }
}

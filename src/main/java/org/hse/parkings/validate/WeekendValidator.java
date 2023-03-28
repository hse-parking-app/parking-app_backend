package org.hse.parkings.validate;

import org.hse.parkings.model.Reservation;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class WeekendValidator implements ConstraintValidator<NotWeekends, Reservation> {

    String message;

    @Override
    public void initialize(NotWeekends constraintAnnotation) {
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

        boolean isValidStartTime = reservation.getStartTime().getDayOfWeek().getValue() < 6;
        boolean isValidEndTime = reservation.getEndTime().getDayOfWeek().getValue() < 6;

        return isValidStartTime && isValidEndTime;
    }
}

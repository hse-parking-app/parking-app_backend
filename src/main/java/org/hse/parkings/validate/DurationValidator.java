package org.hse.parkings.validate;

import org.hse.parkings.model.Reservation;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;

@Component
public class DurationValidator implements ConstraintValidator<DurationIsLess24Hours, Reservation> {

    String message;

    @Override
    public void initialize(DurationIsLess24Hours constraintAnnotation) {
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

        Duration duration = Duration.between(reservation.getStartTime(), reservation.getEndTime());
        return duration.toHours() < 24L && !duration.isNegative();
    }
}

package org.hse.parkings.validate;

import org.hse.parkings.model.Reservation;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;

@Component
public class DurationValidator implements ConstraintValidator<DurationIsLess24Hours, Reservation> {

    @Override
    public boolean isValid(Reservation reservation, ConstraintValidatorContext constraintValidatorContext) {
        Duration duration = Duration.between(reservation.getStartTime(), reservation.getEndTime());
        return duration.toHours() <= 24L && !duration.isNegative();
    }
}

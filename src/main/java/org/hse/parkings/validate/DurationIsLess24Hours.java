package org.hse.parkings.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DurationValidator.class)
public @interface DurationIsLess24Hours {

    String message() default "{org.hse.parkings.validate.DurationIsLess24Hours.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

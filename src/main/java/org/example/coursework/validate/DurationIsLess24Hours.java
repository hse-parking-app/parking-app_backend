/*
 * Copyright (c) 2021-2021.
 * Written by Maksim Stepanenko <stepanenko-qa@yandex.ru>
 */

package org.example.coursework.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DurationValidator.class)
public @interface DurationIsLess24Hours {
    String message() default "Duration between start time and end time must be more than 0 and less than 24 hours";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

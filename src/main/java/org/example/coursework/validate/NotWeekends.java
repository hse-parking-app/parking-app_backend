package org.example.coursework.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WeekendValidator.class)
public @interface NotWeekends {
    String message() default "Cannot book a parking spot on Saturday and Sunday";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

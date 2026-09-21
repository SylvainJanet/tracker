package fr.sylvainjanet.tracker.journal.adapter.in.web.validator.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import fr.sylvainjanet.tracker.journal.adapter.in.web.validator.WeightValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD, PARAMETER, RECORD_COMPONENT, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = WeightValidator.class)
@Documented
public @interface WeightValid {
    String message() default "{weight.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

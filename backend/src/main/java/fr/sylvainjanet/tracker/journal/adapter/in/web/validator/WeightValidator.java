package fr.sylvainjanet.tracker.journal.adapter.in.web.validator;

import fr.sylvainjanet.tracker.journal.adapter.in.web.validator.annotation.WeightValid;
import fr.sylvainjanet.tracker.journal.domain.error.WeightValidationError;
import fr.sylvainjanet.tracker.journal.domain.value.Weight;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Set;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

public final class WeightValidator implements ConstraintValidator<WeightValid, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Set<WeightValidationError> errors = Weight.validateWeightInKg(value);

        if (errors.isEmpty()) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        for (WeightValidationError error : errors) {
            addViolation(context, error);
        }

        return false;
    }

    private static void addViolation(
            ConstraintValidatorContext context, WeightValidationError error) {
        var hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
        switch (error.kind()) {
            case POSITIVE ->
                    hibernateContext
                            .buildConstraintViolationWithTemplate("{weight.positive}")
                            .addConstraintViolation();

            case MULTIPLE_OF_GRAMS_UNIT -> {
                hibernateContext.addMessageParameter("gramsUnit", error.metadata().gramsUnit());

                hibernateContext
                        .buildConstraintViolationWithTemplate("{weight.multiple-of-grams-unit}")
                        .addConstraintViolation();
            }
            case INTEGER ->
                    hibernateContext
                            .buildConstraintViolationWithTemplate("{weight.integer}")
                            .addConstraintViolation();
        }
    }
}

package fr.sylvainjanet.tracker.journal.domain;

import fr.sylvainjanet.tracker.journal.domain.error.WeightValidationError;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Set;

public final class Weight {

    private static final long GRAMS_MEASURABLE_UNIT = 50L;

    private final long grams;

    private Weight(BigDecimal grams) {
        if (!validateWeightInGrams(grams).isEmpty()) {
            throw new IllegalArgumentException(
                    "weight must be a positive number of grams that is a multiple of "
                            + GRAMS_MEASURABLE_UNIT
                            + " grams");
        }
        this.grams = grams.longValueExact();
    }

    public static Weight of(BigDecimal kilograms) {
        BigDecimal grams = kilograms.multiply(BigDecimal.valueOf(1000));
        return new Weight(grams);
    }

    private static Set<WeightValidationError> validateWeightInGrams(BigDecimal grams) {
        Set<WeightValidationError> errors = new java.util.HashSet<>();
        try {
            long gramsLong = grams.longValueExact();
            if (gramsLong <= 0) {
                errors.add(WeightValidationError.positive());
            }
            if (gramsLong % GRAMS_MEASURABLE_UNIT != 0) {
                errors.add(WeightValidationError.multipleOfGramsUnit((int) GRAMS_MEASURABLE_UNIT));
            }
        } catch (ArithmeticException _) {
            errors.add(WeightValidationError.multipleOfGramsUnit((int) GRAMS_MEASURABLE_UNIT));
        }
        return Set.copyOf(errors);
    }

    public static Set<WeightValidationError> validateWeightInKg(BigDecimal kilograms) {
        BigDecimal grams = kilograms.multiply(BigDecimal.valueOf(1000));
        return validateWeightInGrams(grams);
    }

    public BigDecimal inKilograms() {
        return BigDecimal.valueOf(grams, 3).setScale(2, RoundingMode.UNNECESSARY);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Weight weight = (Weight) o;
        return Objects.equals(grams, weight.grams);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(grams);
    }
}

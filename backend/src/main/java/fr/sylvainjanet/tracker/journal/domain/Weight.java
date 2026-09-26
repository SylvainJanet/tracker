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
        validateWeightInGramsOrThrow(grams);
        this.grams = grams.longValueExact();
    }

    private static void validateWeightInGramsOrThrow(BigDecimal grams) {
        Set<WeightValidationError> errors = validateWeightInGrams(grams);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(
                    "weight must be a positive number of grams that is a multiple of "
                            + GRAMS_MEASURABLE_UNIT
                            + " grams");
        }
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

    private static BigDecimal toKilogramsUnchecked(BigDecimal grams) {
        return BigDecimal.valueOf(grams.longValueExact())
                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.UNNECESSARY);
    }

    private static BigDecimal toGramsUnchecked(BigDecimal kilograms) {
        return kilograms.multiply(BigDecimal.valueOf(1000));
    }

    public static BigDecimal toKilograms(BigDecimal grams) {
        validateWeightInGramsOrThrow(grams);
        return toKilogramsUnchecked(grams);
    }

    public static BigDecimal toGrams(BigDecimal kilograms) {
        BigDecimal grams = toGramsUnchecked(kilograms);
        validateWeightInGramsOrThrow(grams);
        return grams.setScale(0, RoundingMode.UNNECESSARY);
    }

    public static Set<WeightValidationError> validateWeightInKg(BigDecimal kilograms) {
        BigDecimal grams = toGramsUnchecked(kilograms);
        return validateWeightInGrams(grams);
    }

    public static Weight of(BigDecimal kilograms) {
        BigDecimal grams = toGrams(kilograms);
        return new Weight(grams);
    }

    public BigDecimal inKilograms() {
        return toKilograms(BigDecimal.valueOf(grams));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Weight weight = (Weight) o;
        return grams == weight.grams;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(grams);
    }
}

package fr.sylvainjanet.tracker.journal.domain.value;

import fr.sylvainjanet.tracker.journal.domain.error.WeightValidationError;
import fr.sylvainjanet.tracker.journal.domain.error.WeightValidationErrorKind;
import fr.sylvainjanet.tracker.journal.domain.error.WeightValidationErrorMetadata;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.object.DomainValueObject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Weight
        implements DomainValueObject<
                Weight, WeightValidationErrorKind, WeightValidationErrorMetadata> {

    private static final long GRAMS_MEASURABLE_UNIT = 50L;

    private final long grams;

    private Weight(long grams) {
        this.grams = grams;
        DomainValidator.validate(this);
    }

    @Override
    public Set<DomainValidationError<WeightValidationErrorKind, WeightValidationErrorMetadata>>
            validate() {
        return Set.copyOf(validate(grams));
    }

    private static Set<WeightValidationError> validate(long grams) {
        Set<WeightValidationError> errors = new HashSet<>();
        if (grams <= 0) {
            errors.add(WeightValidationError.positive());
        }
        if (grams % GRAMS_MEASURABLE_UNIT != 0) {
            errors.add(WeightValidationError.multipleOfGramsUnit((int) GRAMS_MEASURABLE_UNIT));
        }

        return errors;
    }

    private static Set<WeightValidationError> validate(BigDecimal grams) {
        Set<WeightValidationError> errors = new HashSet<>();
        try {
            long gramsLong = grams.longValueExact();
            errors.addAll(validate(gramsLong));
        } catch (ArithmeticException _) {
            errors.add(WeightValidationError.integer());
        }
        return errors;
    }

    private static BigDecimal toKilogramsUnchecked(BigDecimal grams) {
        return BigDecimal.valueOf(grams.longValueExact())
                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.UNNECESSARY);
    }

    private static BigDecimal toGramsUnchecked(BigDecimal kilograms) {
        return kilograms.multiply(BigDecimal.valueOf(1000));
    }

    public static BigDecimal toKilograms(BigDecimal grams) {
        DomainValidator.throwErrors(Set.copyOf(validate(grams)));
        return toKilogramsUnchecked(grams);
    }

    public static BigDecimal toGrams(BigDecimal kilograms) {
        BigDecimal grams = toGramsUnchecked(kilograms);
        DomainValidator.throwErrors(Set.copyOf(validate(grams)));
        return grams.setScale(0, RoundingMode.UNNECESSARY);
    }

    public static Set<WeightValidationError> validateWeightInKg(BigDecimal kilograms) {
        BigDecimal grams = toGramsUnchecked(kilograms);
        return validate(grams);
    }

    public static Weight create(BigDecimal kilograms) {
        BigDecimal grams = toGramsUnchecked(kilograms);
        DomainValidator.throwErrors(Set.copyOf(validate(grams)));
        return new Weight(grams.longValueExact());
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

    @Override
    public String toString() {
        return "Weight{" + "grams=" + grams + '}';
    }
}

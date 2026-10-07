package fr.sylvainjanet.tracker.journal.domain.error;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;

public final class WeightValidationError
        extends DomainValidationError<WeightValidationErrorKind, WeightValidationErrorMetadata> {

    public WeightValidationError(
            WeightValidationErrorKind kind, WeightValidationErrorMetadata metadata) {
        super(kind, metadata);
    }

    public static WeightValidationError integer() {
        return new WeightValidationError(
                WeightValidationErrorKind.INTEGER,
                new WeightValidationErrorMetadata("Weight must be an integer", null));
    }

    public static WeightValidationError positive() {
        return new WeightValidationError(
                WeightValidationErrorKind.POSITIVE,
                new WeightValidationErrorMetadata("Weight must be positive", null));
    }

    public static WeightValidationError multipleOfGramsUnit(int gramsUnit) {
        return new WeightValidationError(
                WeightValidationErrorKind.MULTIPLE_OF_GRAMS_UNIT,
                new WeightValidationErrorMetadata(
                        "Grams unit must be a multiple of " + gramsUnit, gramsUnit));
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "WeightValidationError{" + "metadata=" + metadata + ", kind=" + kind + '}';
    }
}

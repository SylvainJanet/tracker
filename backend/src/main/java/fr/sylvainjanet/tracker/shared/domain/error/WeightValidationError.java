package fr.sylvainjanet.tracker.shared.domain.error;

public record WeightValidationError(Kind kind, Integer gramsUnit) {
    public enum Kind {
        POSITIVE,
        MULTIPLE_OF_GRAMS_UNIT
    }

    public static WeightValidationError positive() {
        return new WeightValidationError(Kind.POSITIVE, null);
    }

    public static WeightValidationError multipleOfGramsUnit(int gramsUnit) {
        return new WeightValidationError(Kind.MULTIPLE_OF_GRAMS_UNIT, gramsUnit);
    }
}

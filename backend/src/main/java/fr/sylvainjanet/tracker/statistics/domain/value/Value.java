package fr.sylvainjanet.tracker.statistics.domain.value;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;
import static java.util.stream.Collectors.toSet;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Value implements DomainGenericValueObject<Value> {

    private final ExactValue exactValue;
    private final Set<Approximation> approximations;

    private Value(ExactValue exactValue, Set<Approximation> approximations) {
        this.exactValue = exactValue;
        this.approximations =
                approximations == null
                        ? null
                        : Collections.unmodifiableSet(new HashSet<>(approximations));
        DomainValidator.validate(this);
    }

    @Override
    public Set<
                    DomainValidationError<
                            GenericDomainValidationErrorKind, GenericDomainValidationErrorMessage>>
            validate() {
        Set<
                        DomainValidationError<
                                GenericDomainValidationErrorKind,
                                GenericDomainValidationErrorMessage>>
                errors = new HashSet<>();

        if (exactValue == null) {
            errors.add(genericError("Exact value must not be null"));
        }
        if (approximations == null) {
            errors.add(genericError("Approximations must not be null"));
        }
        if (exactValue == null || approximations == null) {
            return errors;
        }

        for (Approximation approximation : approximations) {
            if (approximation == null) {
                errors.add(genericError("Approximation must not be null"));
            }
        }
        if (!errors.isEmpty()) {
            return errors;
        }

        Set<CalculationRounding> roundingSet =
                approximations.stream().map(Approximation::rounding).collect(toSet());
        if (roundingSet.size() != approximations.size()) {
            throw new IllegalArgumentException("Approximations must have unique rounding values");
        }

        approximations.forEach(
                approximation -> {
                    Approximation expectedApproximation =
                            exactValue.approximate(approximation.rounding());
                    if (!expectedApproximation.equals(approximation)) {
                        throw new IllegalArgumentException(
                                "Approximations should match the exact value for the same rounding");
                    }
                });

        return errors;
    }

    public static Value create(ExactValue exactValue, Set<Approximation> approximations) {
        return new Value(exactValue, approximations);
    }

    public static Value create(ExactValue value) {
        return new Value(value, Set.of());
    }

    public static Value create(BigDecimal value) {
        ExactValue exactValue = Fraction.create(value);
        return new Value(exactValue, Set.of());
    }

    public static Value create(int value) {
        ExactValue exactValue = Fraction.create(value);
        return new Value(exactValue, Set.of());
    }

    public static Value createByRounding(ExactValue value, Set<CalculationRounding> roundings) {
        Set<Approximation> approximations =
                roundings.stream().map(value::approximate).collect(toSet());
        return new Value(value, approximations);
    }

    public Set<CalculationRounding> roundings() {
        return approximations.stream().map(Approximation::rounding).collect(toSet());
    }

    public ExactValue exactValue() {
        return exactValue;
    }

    public Set<Approximation> approximations() {
        return approximations;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Value) obj;
        return Objects.equals(this.exactValue, that.exactValue)
                && Objects.equals(this.approximations, that.approximations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exactValue, approximations);
    }

    @Override
    public String toString() {
        return "Value["
                + "exactValue="
                + exactValue
                + ", "
                + "approximations="
                + approximations
                + ']';
    }
}

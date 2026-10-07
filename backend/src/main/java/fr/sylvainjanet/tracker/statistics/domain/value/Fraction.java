package fr.sylvainjanet.tracker.statistics.domain.value;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Fraction implements ExactValue, DomainGenericValueObject<Fraction> {

    public static final Fraction ZERO = Fraction.create(BigDecimal.ZERO);

    private final BigDecimal numerator;
    private final BigDecimal denominator;

    private Fraction(BigDecimal numerator, BigDecimal denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
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

        if (numerator == null) {
            errors.add(genericError("Numerator must not be null"));
        }
        if (denominator == null) {
            errors.add(genericError("Denominator must not be null"));
        }
        if (denominator != null && denominator.compareTo(BigDecimal.ZERO) == 0) {
            errors.add(genericError("Denominator must not be zero"));
        }

        return errors;
    }

    public static Fraction create(BigDecimal value) {
        return new Fraction(value, BigDecimal.ONE);
    }

    public static Fraction create(int value) {
        return new Fraction(BigDecimal.valueOf(value), BigDecimal.ONE);
    }

    public static Fraction create(BigDecimal numerator, BigDecimal denominator) {
        return new Fraction(numerator, denominator);
    }

    public Approximation approximate(CalculationRounding rounding) {
        Objects.requireNonNull(rounding, "Rounding must not be null");
        BigDecimal approximation =
                numerator.divide(denominator, rounding.scale(), rounding.roundingMode());
        return Approximation.create(approximation, rounding);
    }

    public BigDecimal numerator() {
        return numerator;
    }

    public BigDecimal denominator() {
        return denominator;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Fraction) obj;
        return Objects.equals(this.numerator, that.numerator)
                && Objects.equals(this.denominator, that.denominator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public String toString() {
        return "Fraction[" + "numerator=" + numerator + ", " + "denominator=" + denominator + ']';
    }
}

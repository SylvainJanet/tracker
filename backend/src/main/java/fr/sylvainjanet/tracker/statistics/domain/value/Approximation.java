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

public final class Approximation implements DomainGenericValueObject<Approximation> {

    private final BigDecimal value;
    private final CalculationRounding rounding;

    private Approximation(BigDecimal value, CalculationRounding rounding) {
        this.value = value;
        this.rounding = rounding;
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

        if (value == null) {
            errors.add(genericError("Approximated value must not be null"));
        }
        if (rounding == null) {
            errors.add(genericError("Rounding must not be null"));
        }
        if (value != null && rounding != null && value.scale() != rounding.scale()) {
            errors.add(genericError("Approximated value scale must be equal to rounding scale"));
        }

        return errors;
    }

    public static Approximation create(BigDecimal value, CalculationRounding rounding) {
        return new Approximation(value, rounding);
    }

    public BigDecimal value() {
        return value;
    }

    public CalculationRounding rounding() {
        return rounding;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Approximation) obj;
        return Objects.equals(this.value, that.value)
                && Objects.equals(this.rounding, that.rounding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, rounding);
    }

    @Override
    public String toString() {
        return "Approximation[" + "value=" + value + ", " + "rounding=" + rounding + ']';
    }
}

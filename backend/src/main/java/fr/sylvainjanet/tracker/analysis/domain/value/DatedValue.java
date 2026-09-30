package fr.sylvainjanet.tracker.analysis.domain.value;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class DatedValue implements DomainGenericValueObject<DatedValue> {

    private final LocalDate date;
    private final BigDecimal value;

    private DatedValue(LocalDate date, BigDecimal value) {
        this.date = date;
        this.value = value;
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

        if (date == null) {
            errors.add(genericError("date must not be null"));
        }
        if (value == null) {
            errors.add(genericError("value must not be null"));
        }

        return errors;
    }

    public static DatedValue create(LocalDate date, BigDecimal value) {
        return new DatedValue(date, value);
    }

    public LocalDate date() {
        return date;
    }

    public BigDecimal value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DatedValue) obj;
        return Objects.equals(this.date, that.date) && Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, value);
    }

    @Override
    public String toString() {
        return "DatedValue[" + "date=" + date + ", " + "value=" + value + ']';
    }
}

package fr.sylvainjanet.tracker.journal.domain.entity;

import fr.sylvainjanet.tracker.journal.domain.value.Weight;
import fr.sylvainjanet.tracker.shared.domain.DateIdentifier;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.object.DomainEntityObject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class WeightMeasurement
        implements DomainEntityObject<
                WeightMeasurement,
                GenericDomainValidationErrorKind,
                GenericDomainValidationErrorMessage,
                DateIdentifier> {

    private final LocalDate date;
    private final Weight weight;

    @Override
    public DateIdentifier identifier() {
        return DateIdentifier.create(date);
    }

    private WeightMeasurement(LocalDate date, Weight weight) {
        this.date = date;
        this.weight = weight;
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
            errors.add(GenericDomainValidationError.genericError("date must not be null"));
        }
        if (weight == null) {
            errors.add(GenericDomainValidationError.genericError("weight must not be null"));
        }

        return errors;
    }

    public static WeightMeasurement create(LocalDate date, Weight weight) {
        return new WeightMeasurement(date, weight);
    }

    public LocalDate date() {
        return date;
    }

    public Weight weight() {
        return weight;
    }

    public BigDecimal weightInKilograms() {
        return weight.inKilograms();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WeightMeasurement that = (WeightMeasurement) o;
        return Objects.equals(this.identifier(), that.identifier());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.identifier());
    }

    @Override
    public String toString() {
        return "WeightMeasurement{" + "date=" + date + ", weight=" + weight + '}';
    }
}

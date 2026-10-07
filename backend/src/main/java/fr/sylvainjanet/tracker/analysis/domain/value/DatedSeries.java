package fr.sylvainjanet.tracker.analysis.domain.value;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;

import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.technical.domain.UnmodifiableList;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DatedSeries extends UnmodifiableList<DatedValue>
        implements DomainGenericValueObject<DatedSeries> {

    private DatedSeries(List<DatedValue> values) {
        super(values);
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

        DatedValue previousValue = null;
        for (DatedValue value : this) {
            if (value == null) {
                errors.add(genericError("dated value must not be null"));
                continue;
            }
            if (previousValue != null && !value.date().isAfter(previousValue.date())) {
                errors.add(
                        genericError("dated values must be ordered by unique chronological dates"));
            }
            previousValue = value;
        }

        return errors;
    }

    public static DatedSeries create(List<DatedValue> values) {
        return new DatedSeries(values);
    }

    public DateRange dateRange() {
        if (this.isEmpty()) {
            return null;
        }
        LocalDate startDate = this.get(0).date();
        LocalDate endDate = this.get(this.size() - 1).date();
        return DateRange.create(startDate, endDate);
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
        return super.toString();
    }
}

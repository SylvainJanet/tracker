package fr.sylvainjanet.tracker.statistics.domain.value;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;

import fr.sylvainjanet.tracker.technical.domain.UnmodifiableList;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class IndexedSeries extends UnmodifiableList<IndexedValue>
        implements DomainGenericValueObject<IndexedSeries> {

    private IndexedSeries(List<IndexedValue> values) {
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

        IndexedValue previousValue = null;
        for (IndexedValue value : this) {
            if (value == null) {
                errors.add(genericError("indexed value must not be null"));
                continue;
            }
            if (previousValue != null && value.index() <= previousValue.index()) {
                errors.add(
                        genericError("indexed values must be ordered by unique ascending indexes"));
            }
            previousValue = value;
        }

        return errors;
    }

    public static IndexedSeries create(List<IndexedValue> values) {
        return new IndexedSeries(values);
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

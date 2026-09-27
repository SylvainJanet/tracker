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

public final class RollingAverages extends UnmodifiableList<RollingAverage>
        implements DomainGenericValueObject<RollingAverages> {

    private RollingAverages(List<RollingAverage> rollingAverages) {
        super(rollingAverages);
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

        RollingAverage previousAverage = null;
        for (RollingAverage average : this) {
            if (average == null) {
                errors.add(genericError("rolling average must not be null"));
                continue;
            }
            if (previousAverage != null && average.windowSize() <= previousAverage.windowSize()) {
                errors.add(
                        genericError(
                                "rolling averages must be ordered by unique ascending window sizes"));
            }
            previousAverage = average;
        }

        return errors;
    }

    public static RollingAverages create(List<RollingAverage> rollingAverages) {
        return new RollingAverages(rollingAverages);
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

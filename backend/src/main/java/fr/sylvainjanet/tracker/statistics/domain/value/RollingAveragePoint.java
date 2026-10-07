package fr.sylvainjanet.tracker.statistics.domain.value;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class RollingAveragePoint implements DomainGenericValueObject<RollingAveragePoint> {

    private final long index;
    private final Set<Long> includedIndexes;
    private final Value rollingAverage;

    private RollingAveragePoint(long index, Set<Long> includedIndexes, Value rollingAverage) {
        this.index = index;
        this.includedIndexes = includedIndexes == null ? null : Set.copyOf(includedIndexes);
        this.rollingAverage = rollingAverage;
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

        if (rollingAverage == null) {
            errors.add(genericError("Rolling average must not be null"));
        }
        if (includedIndexes == null) {
            errors.add(genericError("Included indexes must not be null"));
        }

        return errors;
    }

    public static RollingAveragePoint create(
            long index, Set<Long> includedIndexes, Value rollingAverage) {
        return new RollingAveragePoint(index, includedIndexes, rollingAverage);
    }

    public long index() {
        return index;
    }

    public Set<Long> includedIndexes() {
        return includedIndexes;
    }

    public Value rollingAverage() {
        return rollingAverage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RollingAveragePoint) obj;
        return this.index == that.index
                && Objects.equals(this.includedIndexes, that.includedIndexes)
                && Objects.equals(this.rollingAverage, that.rollingAverage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, includedIndexes, rollingAverage);
    }

    @Override
    public String toString() {
        return "RollingAveragePoint["
                + "index="
                + index
                + ", "
                + "includedIndexes="
                + includedIndexes
                + ", "
                + "rollingAverage="
                + rollingAverage
                + ']';
    }
}

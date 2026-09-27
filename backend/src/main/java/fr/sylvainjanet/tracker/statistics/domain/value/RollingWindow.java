package fr.sylvainjanet.tracker.statistics.domain.value;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class RollingWindow implements DomainGenericValueObject<RollingWindow> {

    private final long size;

    private RollingWindow(long size) {
        this.size = size;
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

        if (size <= 0) {
            errors.add(
                    GenericDomainValidationError.genericError(
                            "rolling window size must be positive"));
        }

        return errors;
    }

    public static RollingWindow create(long size) {
        return new RollingWindow(size);
    }

    public long startFromEnd(long end) {
        return Math.subtractExact(end, size - 1L);
    }

    public long size() {
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RollingWindow) obj;
        return this.size == that.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size);
    }

    @Override
    public String toString() {
        return "RollingWindow[" + "size=" + size + ']';
    }
}

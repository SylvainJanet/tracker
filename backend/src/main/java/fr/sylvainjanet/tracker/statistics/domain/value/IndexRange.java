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

public final class IndexRange implements DomainGenericValueObject<IndexRange> {

    private final long start;
    private final long end;

    private IndexRange(long start, long end) {
        this.start = start;
        this.end = end;
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

        if (start > end) {
            errors.add(genericError("first index must not be after last index"));
        }

        return errors;
    }

    public static IndexRange create(long firstIndex, long lastIndex) {
        return new IndexRange(firstIndex, lastIndex);
    }

    public boolean contains(long index) {
        return index >= start && index <= end;
    }

    public long start() {
        return start;
    }

    public long end() {
        return end;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (IndexRange) obj;
        return this.start == that.start && this.end == that.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "IndexRange[" + "start=" + start + ", " + "end=" + end + ']';
    }
}

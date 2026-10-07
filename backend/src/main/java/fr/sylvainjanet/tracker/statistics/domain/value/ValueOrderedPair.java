package fr.sylvainjanet.tracker.statistics.domain.value;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;

import fr.sylvainjanet.tracker.technical.domain.MultiSet;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ValueOrderedPair implements DomainGenericValueObject<ValueOrderedPair> {

    private final Value first;
    private final Value second;

    private ValueOrderedPair(Value first, Value second) {
        this.first = first;
        this.second = second;
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

        if (first == null) {
            errors.add(genericError("first value must not be null"));
        }
        if (second == null) {
            errors.add(genericError("second value must not be null"));
        }

        if (first != null && second != null) {
            errors.addAll(Values.validate(MultiSet.of(first, second)));
        }

        return errors;
    }

    public static ValueOrderedPair create(Value first, Value second) {
        return new ValueOrderedPair(first, second);
    }

    public Value first() {
        return first;
    }

    public Value second() {
        return second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ValueOrderedPair) obj;
        return Objects.equals(this.first, that.first) && Objects.equals(this.second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "ValueOrderedPair[" + "first=" + first + ", " + "second=" + second + ']';
    }
}

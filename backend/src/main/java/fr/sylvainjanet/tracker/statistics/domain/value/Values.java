package fr.sylvainjanet.tracker.statistics.domain.value;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;
import static java.util.stream.Collectors.toSet;

import fr.sylvainjanet.tracker.technical.domain.ImmutableMultiSet;
import fr.sylvainjanet.tracker.technical.domain.MultiSet;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Values extends ImmutableMultiSet<Value>
        implements DomainGenericValueObject<Values> {

    private Values(MultiSet<Value> values) {
        super(values);
        DomainValidator.validate(this);
    }

    public static Set<
                    DomainValidationError<
                            GenericDomainValidationErrorKind, GenericDomainValidationErrorMessage>>
            validate(Iterable<Value> values) {
        Set<
                        DomainValidationError<
                                GenericDomainValidationErrorKind,
                                GenericDomainValidationErrorMessage>>
                errors = new HashSet<>();

        if (values == null) {
            errors.add(genericError("values must not be null"));
            return errors;
        }

        Set<CalculationRounding> expectedRoundings = null;
        for (Value value : values) {
            if (value == null) {
                errors.add(genericError("value must not be null"));
                continue;
            }

            if (expectedRoundings == null) {
                expectedRoundings = value.roundings();
            } else if (!expectedRoundings.equals(value.roundings())) {
                errors.add(genericError("All values must have the same roundings"));
            }
        }

        return errors;
    }

    @Override
    public Set<
                    DomainValidationError<
                            GenericDomainValidationErrorKind, GenericDomainValidationErrorMessage>>
            validate() {
        return Values.validate(this);
    }

    public static Values create(MultiSet<Value> values) {
        DomainValidator.throwErrors(validate(values));
        return new Values(values);
    }

    public static Values createExactValues(MultiSet<ExactValue> exactValues) {
        Objects.requireNonNull(exactValues, "exact values must not be null");
        return create(exactValues.stream().map(Value::create).collect(MultiSet.toMultiSet()));
    }

    public MultiSet<ExactValue> exactValues() {
        return this.stream().map(Value::exactValue).collect(MultiSet.toMultiSet());
    }

    public Set<CalculationRounding> roundings() {
        return this.stream().flatMap(value -> value.roundings().stream()).collect(toSet());
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

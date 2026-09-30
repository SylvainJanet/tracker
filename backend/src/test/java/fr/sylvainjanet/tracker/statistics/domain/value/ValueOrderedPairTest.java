package fr.sylvainjanet.tracker.statistics.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ValueOrderedPairTest {

    private static final Value FIRST_VALUE = Value.create(Fraction.create(1));
    private static final Value SECOND_VALUE = Value.create(Fraction.create(2));

    @Test
    void createsAnOrderedPair() {
        ValueOrderedPair pair = ValueOrderedPair.create(FIRST_VALUE, SECOND_VALUE);

        assertThat(pair.first()).isEqualTo(FIRST_VALUE);
        assertThat(pair.second()).isEqualTo(SECOND_VALUE);
    }

    @Test
    void rejectsMissingValuesTogether() {
        assertThatThrownBy(() -> ValueOrderedPair.create(null, null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("first value must not be null")
                .hasMessageContaining("second value must not be null");
    }

    @Test
    void requiresBothValuesToHaveTheSameRoundings() {
        Value first =
                Value.createByRounding(Fraction.create(1), Set.of(CalculationRounding.PRECISE));
        Value second =
                Value.createByRounding(Fraction.create(2), Set.of(CalculationRounding.PRETTY));

        assertThatThrownBy(() -> ValueOrderedPair.create(first, second))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("All values must have the same roundings");
    }

    @Test
    void hasOrderedValueSemantics() {
        ValueOrderedPair first = ValueOrderedPair.create(FIRST_VALUE, SECOND_VALUE);
        ValueOrderedPair equal = ValueOrderedPair.create(FIRST_VALUE, SECOND_VALUE);
        ValueOrderedPair reversed = ValueOrderedPair.create(SECOND_VALUE, FIRST_VALUE);

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(reversed)
                .isNotNull();
    }
}

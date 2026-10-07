package fr.sylvainjanet.tracker.statistics.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.MultiSet;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ValuesTest {

    private static final ExactValue FIRST_EXACT_VALUE = Fraction.create(1);
    private static final ExactValue SECOND_EXACT_VALUE = Fraction.create(2);
    private static final Value FIRST_VALUE =
            Value.createByRounding(FIRST_EXACT_VALUE, Set.of(CalculationRounding.PRETTY));
    private static final Value SECOND_VALUE =
            Value.createByRounding(SECOND_EXACT_VALUE, Set.of(CalculationRounding.PRETTY));

    @Test
    void createsAValueMultisetThatPreservesDuplicates() {
        Values values = Values.create(MultiSet.of(FIRST_VALUE, FIRST_VALUE, SECOND_VALUE));

        assertThat(values.size()).isEqualTo(3);
        assertThat(values.distinctSize()).isEqualTo(2);
        assertThat(values.count(FIRST_VALUE)).isEqualTo(2);
        assertThat(values.count(SECOND_VALUE)).isEqualTo(1);
        assertThat(values.roundings()).containsExactly(CalculationRounding.PRETTY);
        assertThat(values.exactValues().count(FIRST_EXACT_VALUE)).isEqualTo(2);
        assertThat(values.exactValues().count(SECOND_EXACT_VALUE)).isEqualTo(1);
    }

    @Test
    void createsValuesFromExactValuesAndPreservesDuplicates() {
        Values values =
                Values.createExactValues(
                        MultiSet.of(FIRST_EXACT_VALUE, FIRST_EXACT_VALUE, SECOND_EXACT_VALUE));

        assertThat(values.size()).isEqualTo(3);
        assertThat(values.count(Value.create(FIRST_EXACT_VALUE))).isEqualTo(2);
        assertThat(values.count(Value.create(SECOND_EXACT_VALUE))).isEqualTo(1);
        assertThat(values.roundings()).isEmpty();
    }

    @Test
    void acceptsAnEmptyMultiset() {
        assertThatCode(() -> Values.create(MultiSet.of())).doesNotThrowAnyException();
    }

    @Test
    void rejectsANullValueMultiset() {
        assertThatThrownBy(() -> Values.create(null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("values must not be null");
    }

    @Test
    void rejectsANullExactValueMultiset() {
        assertThatThrownBy(() -> Values.createExactValues(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("exact values must not be null");
    }

    @Test
    void requiresEveryValueToHaveTheSameRoundings() {
        Value preciseValue =
                Value.createByRounding(SECOND_EXACT_VALUE, Set.of(CalculationRounding.PRECISE));

        MultiSet<Value> values = MultiSet.of(FIRST_VALUE, preciseValue);
        assertThatThrownBy(() -> Values.create(values))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("All values must have the same roundings");
    }

    @Test
    void copiesItsInputAndDoesNotExposeMutableState() {
        MultiSet<Value> suppliedValues = MultiSet.of(FIRST_VALUE, SECOND_VALUE);
        Values values = Values.create(suppliedValues);

        suppliedValues.clear();
        MultiSet<ExactValue> exactValues = values.exactValues();
        exactValues.clear();

        assertThat(values.size()).isEqualTo(2);
        assertThat(values.count(FIRST_VALUE)).isEqualTo(1);
        assertThat(values.count(SECOND_VALUE)).isEqualTo(1);
        Set<Value> distinctValues = values.distinctElements();
        assertThatThrownBy(distinctValues::clear)
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void hasMultisetValueSemantics() {
        Values first = Values.create(MultiSet.of(FIRST_VALUE, FIRST_VALUE, SECOND_VALUE));
        Values equal = Values.create(MultiSet.of(SECOND_VALUE, FIRST_VALUE, FIRST_VALUE));
        Values different = Values.create(MultiSet.of(FIRST_VALUE, SECOND_VALUE));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(different)
                .isNotNull();
    }
}

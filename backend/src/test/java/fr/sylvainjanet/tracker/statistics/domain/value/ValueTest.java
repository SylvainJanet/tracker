package fr.sylvainjanet.tracker.statistics.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ValueTest {

    private static final ExactValue EXACT_VALUE =
            Fraction.create(new BigDecimal("1"), new BigDecimal("3"));
    private static final Approximation PRECISE_APPROXIMATION =
            EXACT_VALUE.approximate(CalculationRounding.PRECISE);
    private static final Approximation PRETTY_APPROXIMATION =
            EXACT_VALUE.approximate(CalculationRounding.PRETTY);

    @Test
    void createsAValueFromAnExactValue() {
        Value value = Value.create(EXACT_VALUE);

        assertThat(value.exactValue()).isEqualTo(EXACT_VALUE);
        assertThat(value.approximations()).isEmpty();
        assertThat(value.roundings()).isEmpty();
    }

    @Test
    void createsAnExactValueFromADecimal() {
        Value value = Value.create(new BigDecimal("12.34"));

        assertThat(value.exactValue()).isEqualTo(Fraction.create(new BigDecimal("12.34")));
        assertThat(value.approximations()).isEmpty();
    }

    @Test
    void createsAnExactValueFromAnInteger() {
        Value value = Value.create(12);

        assertThat(value.exactValue()).isEqualTo(Fraction.create(12));
        assertThat(value.approximations()).isEmpty();
    }

    @Test
    void createsAValueWithExplicitApproximations() {
        Value value =
                Value.create(EXACT_VALUE, Set.of(PRECISE_APPROXIMATION, PRETTY_APPROXIMATION));

        assertThat(value.exactValue()).isEqualTo(EXACT_VALUE);
        assertThat(value.approximations())
                .containsExactlyInAnyOrder(PRECISE_APPROXIMATION, PRETTY_APPROXIMATION);
        assertThat(value.roundings())
                .containsExactlyInAnyOrder(CalculationRounding.PRECISE, CalculationRounding.PRETTY);
    }

    @Test
    void createsEveryRequestedApproximation() {
        Value value = Value.createByRounding(EXACT_VALUE, CalculationRounding.all());

        assertThat(value.exactValue()).isEqualTo(EXACT_VALUE);
        assertThat(value.approximations())
                .containsExactlyInAnyOrder(PRECISE_APPROXIMATION, PRETTY_APPROXIMATION);
        assertThat(value.roundings())
                .containsExactlyInAnyOrderElementsOf(CalculationRounding.all());
    }

    @Test
    void rejectsMissingExactValueAndApproximationsTogether() {
        assertThatThrownBy(() -> Value.create(null, null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Exact value must not be null")
                .hasMessageContaining("Approximations must not be null");
    }

    @Test
    void rejectsANullApproximation() {
        Set<Approximation> approximations = new HashSet<>();
        approximations.add(null);

        assertThatThrownBy(() -> Value.create(EXACT_VALUE, approximations))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Approximation must not be null");
    }

    @Test
    void rejectsDuplicateRoundingValues() {
        Approximation differentPrettyApproximation =
                Approximation.create(new BigDecimal("0.34"), CalculationRounding.PRETTY);

        Set<Approximation> approximations =
                Set.of(PRETTY_APPROXIMATION, differentPrettyApproximation);
        assertThatThrownBy(() -> Value.create(EXACT_VALUE, approximations))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Approximations must have unique rounding values");
    }

    @Test
    void rejectsAnApproximationThatDoesNotMatchTheExactValue() {
        Approximation incorrectApproximation =
                Approximation.create(new BigDecimal("0.34"), CalculationRounding.PRETTY);

        Set<Approximation> approximations = Set.of(incorrectApproximation);
        assertThatThrownBy(() -> Value.create(EXACT_VALUE, approximations))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Approximations should match the exact value for the same rounding");
    }

    @Test
    void protectsItsApproximationsFromMutation() {
        Set<Approximation> suppliedApproximations = new HashSet<>(Set.of(PRETTY_APPROXIMATION));
        Value value = Value.create(EXACT_VALUE, suppliedApproximations);

        suppliedApproximations.clear();

        Set<Approximation> approximations = value.approximations();
        assertThat(approximations).containsExactly(PRETTY_APPROXIMATION);
        assertThatThrownBy(approximations::clear)
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void hasValueSemantics() {
        Value first = Value.create(EXACT_VALUE);
        Value equal = Value.create(EXACT_VALUE);
        Value differentExactValue =
                Value.create(Fraction.create(new BigDecimal("2"), new BigDecimal("3")));
        Value differentApproximations = Value.create(EXACT_VALUE, Set.of(PRETTY_APPROXIMATION));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(differentExactValue)
                .isNotEqualTo(differentApproximations)
                .isNotNull();
    }
}

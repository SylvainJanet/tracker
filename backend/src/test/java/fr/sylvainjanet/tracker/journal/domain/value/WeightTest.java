package fr.sylvainjanet.tracker.journal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.journal.domain.error.WeightValidationError;
import fr.sylvainjanet.tracker.journal.domain.value.Weight;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WeightTest {

    @ParameterizedTest
    @MethodSource("validWeights")
    void createsAWeightFromValidKilograms(BigDecimal kilograms, BigDecimal expectedKilograms) {
        Weight weight = Weight.create(kilograms);

        assertThat(weight.inKilograms()).isEqualTo(expectedKilograms);
    }

    @Test
    void reportsNoValidationErrorForAValidWeight() {
        assertThat(Weight.validateWeightInKg(new BigDecimal("75.50"))).isEmpty();
    }

    @Test
    void reportsThatAWeightMustBePositive() {
        assertThat(Weight.validateWeightInKg(BigDecimal.ZERO))
                .containsExactly(WeightValidationError.positive());
    }

    @Test
    void reportsThatAWeightMustContainAWholeNumberOfGrams() {
        assertThat(Weight.validateWeightInKg(new BigDecimal("75.1234")))
                .containsExactly(WeightValidationError.integer());
    }

    @Test
    void reportsThatAWeightMustUseFiftyGramIncrements() {
        assertThat(Weight.validateWeightInKg(new BigDecimal("75.123")))
                .containsExactly(WeightValidationError.multipleOfGramsUnit(50));
    }

    @Test
    void reportsEveryViolatedWeightInvariant() {
        assertThat(Weight.validateWeightInKg(new BigDecimal("-75.123")))
                .containsExactlyInAnyOrder(
                        WeightValidationError.positive(),
                        WeightValidationError.multipleOfGramsUnit(50));
    }

    @ParameterizedTest
    @MethodSource("invalidWeights")
    void rejectsInvalidWeights(BigDecimal kilograms, String expectedMessage) {
        assertThatThrownBy(() -> Weight.create(kilograms))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void convertsGramsToKilograms() {
        assertThat(Weight.toKilograms(new BigDecimal("75500"))).isEqualTo(new BigDecimal("75.50"));
    }

    @Test
    void rejectsInvalidGramsDuringConversionToKilograms() {
        BigDecimal grams = new BigDecimal("75501");
        assertThatThrownBy(() -> Weight.toKilograms(grams))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Grams unit must be a multiple of 50");
    }

    @Test
    void convertsKilogramsToGrams() {
        assertThat(Weight.toGrams(new BigDecimal("75.50"))).isEqualTo(new BigDecimal("75500"));
    }

    @Test
    void rejectsInvalidKilogramsDuringConversionToGrams() {
        BigDecimal kilograms = new BigDecimal("75.1234");
        assertThatThrownBy(() -> Weight.toGrams(kilograms))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Weight must be an integer");
    }

    @Test
    void hasValueSemantics() {
        Weight first = Weight.create(new BigDecimal("75.50"));
        Weight equal = Weight.create(new BigDecimal("75.500"));
        Weight different = Weight.create(new BigDecimal("80.00"));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(different)
                .isNotNull();
    }

    private static Stream<Arguments> validWeights() {
        return Stream.of(
                Arguments.of(new BigDecimal("12.50"), new BigDecimal("12.50")),
                Arguments.of(new BigDecimal("75.25"), new BigDecimal("75.25")),
                Arguments.of(new BigDecimal("75.500"), new BigDecimal("75.50")));
    }

    private static Stream<Arguments> invalidWeights() {
        return Stream.of(
                Arguments.of(BigDecimal.ZERO, "Weight must be positive"),
                Arguments.of(new BigDecimal("-75.50"), "Weight must be positive"),
                Arguments.of(new BigDecimal("75.1234"), "Weight must be an integer"),
                Arguments.of(new BigDecimal("75.123"), "Grams unit must be a multiple of 50"));
    }
}

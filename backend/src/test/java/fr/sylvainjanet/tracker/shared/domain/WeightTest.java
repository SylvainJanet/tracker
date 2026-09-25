package fr.sylvainjanet.tracker.shared.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.sylvainjanet.tracker.shared.domain.error.WeightValidationError;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class WeightTest {

    @ParameterizedTest
    @MethodSource("validWeights")
    void shouldCreateWeightFromFinitePositiveKilograms(BigDecimal kilograms) {
        Weight weight = Weight.of(kilograms);
        BigDecimal expectedRounded = kilograms.setScale(2, RoundingMode.UNNECESSARY);

        assertEquals(expectedRounded, weight.inKilograms());
    }

    @ParameterizedTest
    @MethodSource("negativeWeights")
    void shouldRejectNonPositiveOrNonFiniteKilograms(BigDecimal kilograms) {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> Weight.of(kilograms));

        assertEquals(
                "weight must be a positive number of grams that is a multiple of 50 grams",
                exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidScaleWeights")
    void shouldRejectInvalidScaleOfGrams(BigDecimal kilograms) {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> Weight.of(kilograms));

        assertEquals(
                "weight must be a positive number of grams that is a multiple of 50 grams",
                exception.getMessage());
    }

    @Test
    void shouldValidateWeightFromValidKilograms() {
        Set<WeightValidationError> errors = Weight.validateWeightInKg(BigDecimal.valueOf(75.5f));

        assertEquals(0, errors.size());
    }

    @Test
    void shouldInvalidateNegativeWeight() {
        Set<WeightValidationError> errors = Weight.validateWeightInKg(BigDecimal.valueOf(-75.5f));

        assertEquals(1, errors.size());
        assertEquals(WeightValidationError.Kind.POSITIVE, errors.iterator().next().kind());
    }

    @Test
    void shouldInvalidateWeightInWrongIncrement() {
        Set<WeightValidationError> errors = Weight.validateWeightInKg(BigDecimal.valueOf(75.1234f));

        assertEquals(1, errors.size());
        assertEquals(
                WeightValidationError.Kind.MULTIPLE_OF_GRAMS_UNIT, errors.iterator().next().kind());
    }

    @Test
    void weightsWithTheSameKilogramsShouldBeEqual() {
        Weight first = Weight.of(BigDecimal.valueOf(75.5f));
        Weight second = Weight.of(BigDecimal.valueOf(75.5f));

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void weightsWithDifferentKilogramsShouldNotBeEqual() {
        Weight first = Weight.of(BigDecimal.valueOf(75.5f));
        Weight second = Weight.of(BigDecimal.valueOf(80.0f));

        assertNotEquals(first, second);
    }

    @Test
    void shouldConvertGramsToKilograms() {
        BigDecimal grams = BigDecimal.valueOf(75500);
        BigDecimal expectedKilograms =
                BigDecimal.valueOf(75.5f).setScale(2, RoundingMode.UNNECESSARY);

        assertEquals(expectedKilograms, Weight.toKilograms(grams));
    }

    @Test
    void shouldFailToConvertInvalidGramsToKilograms() {
        BigDecimal grams = BigDecimal.valueOf(75501);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> Weight.toKilograms(grams));

        assertEquals(
                "weight must be a positive number of grams that is a multiple of 50 grams",
                exception.getMessage());
    }

    @Test
    void shouldConvertKilogramsToGrams() {
        BigDecimal kilograms = BigDecimal.valueOf(75.5f);
        BigDecimal expectedGrams = BigDecimal.valueOf(75500).setScale(0, RoundingMode.UNNECESSARY);

        assertEquals(expectedGrams, Weight.toGrams(kilograms));
    }

    @Test
    void shouldFailToConvertInvalidKilogramsToGrams() {
        BigDecimal kilograms = BigDecimal.valueOf(75.1234f);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> Weight.toGrams(kilograms));

        assertEquals(
                "weight must be a positive number of grams that is a multiple of 50 grams",
                exception.getMessage());
    }

    @Test
    void weightShouldNotEqualNull() {
        assertNotEquals(null, Weight.of(BigDecimal.valueOf(75.5f)));
    }

    private static Stream<BigDecimal> validWeights() {
        return Stream.of(BigDecimal.valueOf(12.5f), BigDecimal.valueOf(75.25f));
    }

    private static Stream<BigDecimal> negativeWeights() {
        return Stream.of(
                BigDecimal.valueOf(0.0f), BigDecimal.valueOf(-0.0f), BigDecimal.valueOf(-1.0f));
    }

    private static Stream<BigDecimal> invalidScaleWeights() {
        return Stream.of(
                BigDecimal.valueOf(0.001f),
                BigDecimal.valueOf(-0.44f),
                BigDecimal.valueOf(75.12345f));
    }
}

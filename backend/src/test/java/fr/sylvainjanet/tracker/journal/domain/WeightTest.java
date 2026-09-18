package fr.sylvainjanet.tracker.journal.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        assertEquals("weight must be a positive number of grams", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidScaleWeights")
    void shouldRejectInvalidScaleOfGrams(BigDecimal kilograms) {
        ArithmeticException exception =
                assertThrows(ArithmeticException.class, () -> Weight.of(kilograms));

        assertEquals("Rounding necessary", exception.getMessage());
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

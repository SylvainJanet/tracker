package fr.sylvainjanet.tracker.tracking.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class WeightTest {

    @ParameterizedTest
    @MethodSource("validWeights")
    void shouldCreateWeightFromFinitePositiveKilograms(float kilograms) {
        Weight weight = Weight.of(kilograms);

        assertEquals(Float.valueOf(kilograms), weight.kilograms());
    }

    @ParameterizedTest
    @MethodSource("invalidWeights")
    void shouldRejectNonPositiveOrNonFiniteKilograms(float kilograms) {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> Weight.of(kilograms));

        assertEquals(
                "weight must be a finite positive number of kilograms", exception.getMessage());
    }

    @Test
    void weightsWithTheSameKilogramsShouldBeEqual() {
        Weight first = Weight.of(75.5f);
        Weight second = Weight.of(75.5f);

        assertEquals(first, second);
        assertEquals(second, first);
    }

    @Test
    void equalWeightsShouldHaveTheSameHashCode() {
        Weight first = Weight.of(75.5f);
        Weight second = Weight.of(75.5f);

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void weightsWithDifferentKilogramsShouldNotBeEqual() {
        Weight first = Weight.of(75.5f);
        Weight second = Weight.of(80.0f);

        assertNotEquals(first, second);
    }

    @Test
    void weightShouldNotEqualNull() {
        assertNotEquals(null, Weight.of(75.5f));
    }

    private static Stream<Float> validWeights() {
        return Stream.of(Float.MIN_VALUE, 0.1f, 75.5f, Float.MAX_VALUE);
    }

    private static Stream<Float> invalidWeights() {
        return Stream.of(
                0.0f, -0.0f, -1.0f, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
    }
}

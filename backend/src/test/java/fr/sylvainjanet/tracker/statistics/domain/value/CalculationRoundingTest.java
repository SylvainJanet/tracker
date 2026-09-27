package fr.sylvainjanet.tracker.statistics.domain.value;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.RoundingMode;
import org.junit.jupiter.api.Test;

class CalculationRoundingTest {

    @Test
    void definesThePreciseRoundingContract() {
        assertThat(CalculationRounding.PRECISE.scale()).isEqualTo(20);
        assertThat(CalculationRounding.PRECISE.roundingMode()).isEqualTo(RoundingMode.HALF_UP);
    }

    @Test
    void definesThePrettyRoundingContract() {
        assertThat(CalculationRounding.PRETTY.scale()).isEqualTo(2);
        assertThat(CalculationRounding.PRETTY.roundingMode()).isEqualTo(RoundingMode.HALF_UP);
    }

    @Test
    void exposesEverySupportedRounding() {
        assertThat(CalculationRounding.all())
                .containsExactlyInAnyOrder(CalculationRounding.PRECISE, CalculationRounding.PRETTY);
    }
}

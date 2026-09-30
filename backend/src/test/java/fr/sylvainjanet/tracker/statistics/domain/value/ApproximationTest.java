package fr.sylvainjanet.tracker.statistics.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ApproximationTest {

    @Test
    void createsAnApproximation() {
        Approximation approximation =
                Approximation.create(new BigDecimal("12.34"), CalculationRounding.PRETTY);

        assertThat(approximation.value()).isEqualTo(new BigDecimal("12.34"));
        assertThat(approximation.rounding()).isEqualTo(CalculationRounding.PRETTY);
    }

    @Test
    void rejectsMissingValueAndRoundingTogether() {
        assertThatThrownBy(() -> Approximation.create(null, null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Approximated value must not be null")
                .hasMessageContaining("Rounding must not be null");
    }

    @Test
    void rejectsAValueWhoseScaleDoesNotMatchItsRounding() {
        BigDecimal value = new BigDecimal("12.3");
        assertThatThrownBy(() -> Approximation.create(value, CalculationRounding.PRETTY))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Approximated value scale must be equal to rounding scale");
    }

    @Test
    void hasValueSemantics() {
        Approximation first =
                Approximation.create(new BigDecimal("12.34"), CalculationRounding.PRETTY);
        Approximation equal =
                Approximation.create(new BigDecimal("12.34"), CalculationRounding.PRETTY);
        Approximation differentValue =
                Approximation.create(new BigDecimal("56.78"), CalculationRounding.PRETTY);
        Approximation differentRounding =
                Approximation.create(
                        new BigDecimal("12.34000000000000000000"), CalculationRounding.PRECISE);

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(differentValue)
                .isNotEqualTo(differentRounding)
                .isNotNull();
    }
}

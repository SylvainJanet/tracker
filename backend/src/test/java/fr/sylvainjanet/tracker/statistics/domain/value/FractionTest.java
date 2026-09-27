package fr.sylvainjanet.tracker.statistics.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class FractionTest {

    @Test
    void createsAFractionFromANumeratorAndDenominator() {
        Fraction fraction = Fraction.create(new BigDecimal("12.34"), new BigDecimal("5.67"));

        assertThat(fraction.numerator()).isEqualTo(new BigDecimal("12.34"));
        assertThat(fraction.denominator()).isEqualTo(new BigDecimal("5.67"));
    }

    @Test
    void createsAFractionFromADecimalValue() {
        Fraction fraction = Fraction.create(new BigDecimal("12.34"));

        assertThat(fraction.numerator()).isEqualTo(new BigDecimal("12.34"));
        assertThat(fraction.denominator()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void createsAFractionFromAnIntegerValue() {
        Fraction fraction = Fraction.create(12);

        assertThat(fraction.numerator()).isEqualTo(new BigDecimal("12"));
        assertThat(fraction.denominator()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void exposesAnExactZeroFraction() {
        assertThat(Fraction.ZERO.numerator()).isEqualTo(BigDecimal.ZERO);
        assertThat(Fraction.ZERO.denominator()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void rejectsMissingNumeratorAndDenominatorTogether() {
        assertThatThrownBy(() -> Fraction.create(null, null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Numerator must not be null")
                .hasMessageContaining("Denominator must not be null");
    }

    @Test
    void rejectsAZeroDenominator() {
        assertThatThrownBy(() -> Fraction.create(BigDecimal.ONE, BigDecimal.ZERO))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Denominator must not be zero");
    }

    @Test
    void createsThePreciseApproximation() {
        Fraction fraction = Fraction.create(new BigDecimal("2"), new BigDecimal("3"));

        Approximation approximation = fraction.approximate(CalculationRounding.PRECISE);

        assertThat(approximation.value()).isEqualTo(new BigDecimal("0.66666666666666666667"));
        assertThat(approximation.rounding()).isEqualTo(CalculationRounding.PRECISE);
    }

    @Test
    void createsThePrettyApproximation() {
        Fraction fraction = Fraction.create(new BigDecimal("2"), new BigDecimal("3"));

        Approximation approximation = fraction.approximate(CalculationRounding.PRETTY);

        assertThat(approximation.value()).isEqualTo(new BigDecimal("0.67"));
        assertThat(approximation.rounding()).isEqualTo(CalculationRounding.PRETTY);
    }

    @Test
    void rejectsANullRounding() {
        Fraction fraction = Fraction.create(new BigDecimal("2"), new BigDecimal("3"));

        assertThatThrownBy(() -> fraction.approximate(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("Rounding must not be null");
    }

    @Test
    void hasValueSemantics() {
        Fraction first = Fraction.create(new BigDecimal("2"), new BigDecimal("3"));
        Fraction equal = Fraction.create(new BigDecimal("2"), new BigDecimal("3"));
        Fraction differentNumerator = Fraction.create(new BigDecimal("1"), new BigDecimal("3"));
        Fraction differentDenominator = Fraction.create(new BigDecimal("2"), new BigDecimal("5"));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(differentNumerator)
                .isNotEqualTo(differentDenominator)
                .isNotNull();
    }
}

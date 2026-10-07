package fr.sylvainjanet.tracker.statistics.domain.calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.statistics.domain.value.CalculationRounding;
import fr.sylvainjanet.tracker.statistics.domain.value.Fraction;
import fr.sylvainjanet.tracker.statistics.domain.value.Value;
import fr.sylvainjanet.tracker.statistics.domain.value.ValueOrderedPair;
import fr.sylvainjanet.tracker.statistics.domain.value.Values;
import fr.sylvainjanet.tracker.technical.domain.MultiSet;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BasicArithmeticCalculatorTest {

    private static final BasicArithmeticCalculator CALCULATOR =
            BasicArithmeticCalculator.getInstance();

    @Test
    void returnsExactZeroForAnEmptySum() {
        Value sum = CALCULATOR.add(Values.create(MultiSet.of()));

        assertThat(sum).isEqualTo(Value.create(Fraction.ZERO));
    }

    @Test
    void addsFractionsExactlyAndDerivesTheirRequestedApproximations() {
        Fraction oneThird = Fraction.create(new BigDecimal("1"), new BigDecimal("3"));
        Fraction oneSixth = Fraction.create(new BigDecimal("1"), new BigDecimal("6"));
        Values values =
                Values.create(
                        MultiSet.of(
                                Value.createByRounding(oneThird, CalculationRounding.all()),
                                Value.createByRounding(oneSixth, CalculationRounding.all())));

        Value sum = CALCULATOR.add(values);

        Fraction expectedExactSum = Fraction.create(new BigDecimal("9"), new BigDecimal("18"));
        assertThat(sum)
                .isEqualTo(Value.createByRounding(expectedExactSum, CalculationRounding.all()));
    }

    @Test
    void includesEveryOccurrenceWhenAddingValues() {
        Fraction oneHalf = Fraction.create(new BigDecimal("1"), new BigDecimal("2"));
        Value value = Value.create(oneHalf);
        Values values = Values.create(MultiSet.of(value, value));

        Value sum = CALCULATOR.add(values);

        assertThat(sum.exactValue())
                .isEqualTo(Fraction.create(new BigDecimal("4"), new BigDecimal("4")));
    }

    @Test
    void addsSeveralDistinctDecimalValuesExactly() {
        Value sum = CALCULATOR.add(sevenVaryingWeights());

        assertThat(sum).isEqualTo(Value.create(new BigDecimal("565.50")));
    }

    @Test
    void calculatesAnExactAverageFromSeveralValuesAndDerivesBothApproximations() {
        Value sum = CALCULATOR.add(sevenVaryingWeights());

        Value average =
                CALCULATOR.divide(
                        ValueOrderedPair.create(sum, Value.create(7)), CalculationRounding.all());

        Fraction expectedExactAverage =
                Fraction.create(new BigDecimal("565.50"), new BigDecimal("7"));
        assertThat(average)
                .isEqualTo(Value.createByRounding(expectedExactAverage, CalculationRounding.all()));
        assertThat(expectedExactAverage.approximate(CalculationRounding.PRETTY).value())
                .isEqualTo(new BigDecimal("80.79"));
        assertThat(expectedExactAverage.approximate(CalculationRounding.PRECISE).value())
                .isEqualTo(new BigDecimal("80.78571428571428571429"));
    }

    @Test
    void dividesFractionsExactlyAndDerivesTheRequestedApproximations() {
        Value dividend = Value.create(Fraction.create(new BigDecimal("2"), new BigDecimal("3")));
        Value divisor = Value.create(Fraction.create(new BigDecimal("4"), new BigDecimal("5")));

        Value quotient =
                CALCULATOR.divide(
                        ValueOrderedPair.create(dividend, divisor), CalculationRounding.all());

        Fraction expectedExactQuotient =
                Fraction.create(new BigDecimal("10"), new BigDecimal("12"));
        assertThat(quotient)
                .isEqualTo(
                        Value.createByRounding(expectedExactQuotient, CalculationRounding.all()));
    }

    @Test
    void rejectsDivisionByZero() {
        Value dividend = Value.create(1);
        Value divisor = Value.create(Fraction.ZERO);
        ValueOrderedPair pair = ValueOrderedPair.create(dividend, divisor);

        assertThatThrownBy(() -> CALCULATOR.divide(pair, Set.of()))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Denominator must not be zero");
    }

    private static Values sevenVaryingWeights() {
        return Values.create(
                MultiSet.of(
                        decimalValue("80.15"),
                        decimalValue("81.20"),
                        decimalValue("79.85"),
                        decimalValue("82.40"),
                        decimalValue("80.55"),
                        decimalValue("81.05"),
                        decimalValue("80.30")));
    }

    private static Value decimalValue(String value) {
        return Value.create(new BigDecimal(value));
    }
}

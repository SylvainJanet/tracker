package fr.sylvainjanet.tracker.statistics.domain.calculator;

import static java.util.stream.Collectors.toSet;

import fr.sylvainjanet.tracker.statistics.domain.value.Approximation;
import fr.sylvainjanet.tracker.statistics.domain.value.CalculationRounding;
import fr.sylvainjanet.tracker.statistics.domain.value.ExactValue;
import fr.sylvainjanet.tracker.statistics.domain.value.Fraction;
import fr.sylvainjanet.tracker.statistics.domain.value.Value;
import fr.sylvainjanet.tracker.statistics.domain.value.ValueOrderedPair;
import fr.sylvainjanet.tracker.statistics.domain.value.Values;
import java.math.BigDecimal;
import java.util.Set;

public final class BasicArithmeticCalculator {

    private static BasicArithmeticCalculator INSTANCE;

    private BasicArithmeticCalculator() {}

    public static BasicArithmeticCalculator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BasicArithmeticCalculator();
        }
        return INSTANCE;
    }

    private ExactValue addExact(ExactValue x, ExactValue y) {
        if (x instanceof Fraction xFraction && y instanceof Fraction yFraction) {
            BigDecimal xNum = xFraction.numerator();
            BigDecimal xDen = xFraction.denominator();
            BigDecimal yNum = yFraction.numerator();
            BigDecimal yDen = yFraction.denominator();

            BigDecimal numerator = xNum.multiply(yDen).add(yNum.multiply(xDen));
            BigDecimal denominator = xDen.multiply(yDen);

            return Fraction.create(numerator, denominator);
        } else {
            throw new IllegalArgumentException("Unsupported ExactValue types for addition");
        }
    }

    public Value add(Values values) {
        ExactValue sum =
                values.exactValues().stream().reduce(this::addExact).orElseGet(() -> Fraction.ZERO);
        Set<Approximation> approximations =
                values.roundings().stream().map(sum::approximate).collect(toSet());

        return Value.create(sum, approximations);
    }

    private ExactValue divideExact(ExactValue x, ExactValue y) {
        if (x instanceof Fraction xFraction && y instanceof Fraction yFraction) {
            BigDecimal numerator = xFraction.numerator().multiply(yFraction.denominator());
            BigDecimal denominator = xFraction.denominator().multiply(yFraction.numerator());

            return Fraction.create(numerator, denominator);
        } else {
            throw new IllegalArgumentException("Unsupported ExactValue types for division");
        }
    }

    public Value divide(ValueOrderedPair pair, Set<CalculationRounding> roundings) {
        Value x = pair.first();
        Value y = pair.second();

        ExactValue division = divideExact(x.exactValue(), y.exactValue());

        return Value.createByRounding(division, roundings);
    }
}

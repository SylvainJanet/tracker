package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.FractionResult;
import java.math.BigDecimal;

public final class FractionResultBuilder {

    private BigDecimal numerator;
    private BigDecimal denominator;

    private FractionResultBuilder() {}

    public static FractionResultBuilder aFractionResult() {
        return new FractionResultBuilder();
    }

    public FractionResultBuilder withNumerator(BigDecimal numerator) {
        this.numerator = numerator;
        return this;
    }

    public FractionResultBuilder withDenominator(BigDecimal denominator) {
        this.denominator = denominator;
        return this;
    }

    public FractionResult build() {
        return new FractionResult(numerator, denominator);
    }
}

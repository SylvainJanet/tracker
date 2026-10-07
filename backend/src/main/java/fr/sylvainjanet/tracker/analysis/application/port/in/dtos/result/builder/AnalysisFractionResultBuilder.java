package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisFractionResult;
import java.math.BigDecimal;

public final class AnalysisFractionResultBuilder {

    private BigDecimal numerator;
    private BigDecimal denominator;

    private AnalysisFractionResultBuilder() {}

    public static AnalysisFractionResultBuilder anAnalysisFractionResult() {
        return new AnalysisFractionResultBuilder();
    }

    public AnalysisFractionResultBuilder withNumerator(BigDecimal numerator) {
        this.numerator = numerator;
        return this;
    }

    public AnalysisFractionResultBuilder withDenominator(BigDecimal denominator) {
        this.denominator = denominator;
        return this;
    }

    public AnalysisFractionResult build() {
        return new AnalysisFractionResult(numerator, denominator);
    }
}

package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisFractionResponse;
import java.math.BigDecimal;

public final class AnalysisFractionResponseBuilder {

    private BigDecimal numerator;
    private BigDecimal denominator;

    private AnalysisFractionResponseBuilder() {}

    public static AnalysisFractionResponseBuilder anAnalysisFractionResponse() {
        return new AnalysisFractionResponseBuilder();
    }

    public AnalysisFractionResponseBuilder withNumerator(BigDecimal numerator) {
        this.numerator = numerator;
        return this;
    }

    public AnalysisFractionResponseBuilder withDenominator(BigDecimal denominator) {
        this.denominator = denominator;
        return this;
    }

    public AnalysisFractionResponse build() {
        return new AnalysisFractionResponse(numerator, denominator);
    }
}

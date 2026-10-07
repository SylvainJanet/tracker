package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisApproximationResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRoundingResult;
import java.math.BigDecimal;

public final class AnalysisApproximationResultBuilder {

    private BigDecimal value;
    private AnalysisRoundingResult rounding;

    private AnalysisApproximationResultBuilder() {}

    public static AnalysisApproximationResultBuilder anAnalysisApproximationResult() {
        return new AnalysisApproximationResultBuilder();
    }

    public AnalysisApproximationResultBuilder withValue(BigDecimal value) {
        this.value = value;
        return this;
    }

    public AnalysisApproximationResultBuilder withRounding(AnalysisRoundingResult rounding) {
        this.rounding = rounding;
        return this;
    }

    public AnalysisApproximationResult build() {
        return new AnalysisApproximationResult(value, rounding);
    }
}

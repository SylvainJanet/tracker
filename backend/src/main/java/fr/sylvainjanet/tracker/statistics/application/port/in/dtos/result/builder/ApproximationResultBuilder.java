package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ApproximationResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculationRoundingResult;
import java.math.BigDecimal;

public final class ApproximationResultBuilder {

    private BigDecimal value;
    private CalculationRoundingResult rounding;

    private ApproximationResultBuilder() {}

    public static ApproximationResultBuilder anApproximationResult() {
        return new ApproximationResultBuilder();
    }

    public ApproximationResultBuilder withValue(BigDecimal value) {
        this.value = value;
        return this;
    }

    public ApproximationResultBuilder withRounding(CalculationRoundingResult rounding) {
        this.rounding = rounding;
        return this;
    }

    public ApproximationResult build() {
        return new ApproximationResult(value, rounding);
    }
}

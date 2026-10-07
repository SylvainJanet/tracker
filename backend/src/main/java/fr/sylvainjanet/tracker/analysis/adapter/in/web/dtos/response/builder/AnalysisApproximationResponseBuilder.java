package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisApproximationResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRoundingResponse;
import java.math.BigDecimal;

public final class AnalysisApproximationResponseBuilder {

    private BigDecimal value;
    private AnalysisRoundingResponse rounding;

    private AnalysisApproximationResponseBuilder() {}

    public static AnalysisApproximationResponseBuilder anAnalysisApproximationResponse() {
        return new AnalysisApproximationResponseBuilder();
    }

    public AnalysisApproximationResponseBuilder withValue(BigDecimal value) {
        this.value = value;
        return this;
    }

    public AnalysisApproximationResponseBuilder withRounding(AnalysisRoundingResponse rounding) {
        this.rounding = rounding;
        return this;
    }

    public AnalysisApproximationResponse build() {
        return new AnalysisApproximationResponse(value, rounding);
    }
}

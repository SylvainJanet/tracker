package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisApproximationResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisExactValueResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRollingAverageValueResponse;
import java.util.ArrayList;
import java.util.List;

public final class AnalysisRollingAverageValueResponseBuilder {

    private AnalysisExactValueResponse exactValue;
    private final List<AnalysisApproximationResponse> approximations = new ArrayList<>();

    private AnalysisRollingAverageValueResponseBuilder() {}

    public static AnalysisRollingAverageValueResponseBuilder
            anAnalysisRollingAverageValueResponse() {
        return new AnalysisRollingAverageValueResponseBuilder();
    }

    public AnalysisRollingAverageValueResponseBuilder withExactValue(
            AnalysisExactValueResponse exactValue) {
        this.exactValue = exactValue;
        return this;
    }

    public AnalysisRollingAverageValueResponseBuilder withApproximations(
            List<AnalysisApproximationResponse> approximations) {
        this.approximations.addAll(approximations);
        return this;
    }

    public AnalysisRollingAverageValueResponse build() {
        return new AnalysisRollingAverageValueResponse(exactValue, approximations);
    }
}

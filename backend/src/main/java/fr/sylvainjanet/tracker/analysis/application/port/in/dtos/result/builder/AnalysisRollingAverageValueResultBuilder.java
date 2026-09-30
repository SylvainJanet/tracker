package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisApproximationResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisExactValueResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageValueResult;
import java.util.HashSet;
import java.util.Set;

public final class AnalysisRollingAverageValueResultBuilder {

    private AnalysisExactValueResult exactValue;
    private final Set<AnalysisApproximationResult> approximations = new HashSet<>();

    private AnalysisRollingAverageValueResultBuilder() {}

    public static AnalysisRollingAverageValueResultBuilder anAnalysisRollingAverageValueResult() {
        return new AnalysisRollingAverageValueResultBuilder();
    }

    public AnalysisRollingAverageValueResultBuilder withExactValue(
            AnalysisExactValueResult exactValue) {
        this.exactValue = exactValue;
        return this;
    }

    public AnalysisRollingAverageValueResultBuilder withApproximations(
            Set<AnalysisApproximationResult> approximations) {
        this.approximations.addAll(approximations);
        return this;
    }

    public AnalysisRollingAverageValueResult build() {
        return new AnalysisRollingAverageValueResult(exactValue, approximations);
    }
}

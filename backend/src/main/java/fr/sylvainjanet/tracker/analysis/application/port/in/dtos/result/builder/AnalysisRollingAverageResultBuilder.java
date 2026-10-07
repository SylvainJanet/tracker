package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAveragePointResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageResult;
import java.util.ArrayList;
import java.util.List;

public final class AnalysisRollingAverageResultBuilder {

    private long windowSize;
    private final List<AnalysisRollingAveragePointResult> rollingAverages = new ArrayList<>();

    private AnalysisRollingAverageResultBuilder() {}

    public static AnalysisRollingAverageResultBuilder anAnalysisRollingAverageResult() {
        return new AnalysisRollingAverageResultBuilder();
    }

    public AnalysisRollingAverageResultBuilder withWindowSize(long windowSize) {
        this.windowSize = windowSize;
        return this;
    }

    public AnalysisRollingAverageResultBuilder withRollingAverages(
            List<AnalysisRollingAveragePointResult> rollingAverages) {
        this.rollingAverages.addAll(rollingAverages);
        return this;
    }

    public AnalysisRollingAverageResult build() {
        return new AnalysisRollingAverageResult(windowSize, rollingAverages);
    }
}

package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRollingAveragePointResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRollingAverageResponse;
import java.util.ArrayList;
import java.util.List;

public final class AnalysisRollingAverageResponseBuilder {

    private long windowSize;
    private final List<AnalysisRollingAveragePointResponse> rollingAverages = new ArrayList<>();

    private AnalysisRollingAverageResponseBuilder() {}

    public static AnalysisRollingAverageResponseBuilder anAnalysisRollingAverageResponse() {
        return new AnalysisRollingAverageResponseBuilder();
    }

    public AnalysisRollingAverageResponseBuilder withWindowSize(long windowSize) {
        this.windowSize = windowSize;
        return this;
    }

    public AnalysisRollingAverageResponseBuilder withRollingAverages(
            List<AnalysisRollingAveragePointResponse> rollingAverages) {
        this.rollingAverages.addAll(rollingAverages);
        return this;
    }

    public AnalysisRollingAverageResponse build() {
        return new AnalysisRollingAverageResponse(windowSize, rollingAverages);
    }
}

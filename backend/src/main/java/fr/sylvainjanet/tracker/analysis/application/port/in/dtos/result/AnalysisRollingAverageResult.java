package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result;

import java.util.List;

public record AnalysisRollingAverageResult(
        long windowSize, List<AnalysisRollingAveragePointResult> rollingAverages) {}

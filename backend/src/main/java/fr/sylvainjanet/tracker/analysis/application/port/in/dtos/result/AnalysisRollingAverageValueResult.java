package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result;

import java.util.Set;

public record AnalysisRollingAverageValueResult(
        AnalysisExactValueResult exactValue, Set<AnalysisApproximationResult> approximations) {}

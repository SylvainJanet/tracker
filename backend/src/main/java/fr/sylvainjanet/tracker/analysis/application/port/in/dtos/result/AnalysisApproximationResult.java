package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result;

import java.math.BigDecimal;

public record AnalysisApproximationResult(BigDecimal value, AnalysisRoundingResult rounding) {}

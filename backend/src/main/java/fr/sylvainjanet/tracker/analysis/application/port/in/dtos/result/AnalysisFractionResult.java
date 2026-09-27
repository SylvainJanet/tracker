package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result;

import java.math.BigDecimal;

public record AnalysisFractionResult(BigDecimal numerator, BigDecimal denominator)
        implements AnalysisExactValueResult {}

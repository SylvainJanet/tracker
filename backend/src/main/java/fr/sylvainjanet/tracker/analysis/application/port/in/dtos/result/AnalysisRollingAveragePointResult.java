package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result;

import java.time.LocalDate;
import java.util.Set;

public record AnalysisRollingAveragePointResult(
        LocalDate date,
        long dayNumber,
        Set<AnalysisValueResult> includedValues,
        AnalysisRollingAverageValueResult rollingAverage) {}

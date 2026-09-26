package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record GetWeightAnalysisResult(
        LocalDate timelineStartDate,
        AnalysisDateRangeResult dateRange,
        List<AnalysisWeightValueResult> weightValues,
        List<AnalysisWeightRollingAverageResult> rollingAverages) {

    public record AnalysisDateRangeResult(LocalDate startDate, LocalDate endDate) {}

    public record AnalysisWeightValueResult(
            LocalDate date, long dayNumber, BigDecimal weightInKg) {}

    public record AnalysisWeightRollingAverageResult(
            int windowInDays, List<WeightRollingAveragePointResult> points) {

        public AnalysisWeightRollingAverageResult {
            points = List.copyOf(Objects.requireNonNull(points, "points must not be null"));
        }
    }

    public record WeightRollingAveragePointResult(
            LocalDate date, long dayNumber, BigDecimal averageWeightInKg) {}

    public GetWeightAnalysisResult {
        weightValues =
                List.copyOf(
                        Objects.requireNonNull(
                                weightValues, "weight measurements must not be null"));
        rollingAverages =
                List.copyOf(
                        Objects.requireNonNull(
                                rollingAverages, "rolling averages must not be null"));
    }

    /** Returns the globally empty result: null timeline and range with empty value collections. */
    public static GetWeightAnalysisResult noMeasurements() {
        return new GetWeightAnalysisResult(null, null, List.of(), List.of());
    }
}

package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record GetWeightAnalysisResult(
        LocalDate timelineStartDate,
        DateRangeResult range,
        List<WeightMeasurementResult> weightMeasurements,
        List<RollingAverageResult> rollingAverages) {

    public record DateRangeResult(LocalDate startDate, LocalDate endDate) {}

    public record WeightMeasurementResult(LocalDate date, long dayNumber, BigDecimal weightInKg) {}

    public record RollingAverageResult(int windowInDays, List<RollingAveragePointResult> points) {

        public RollingAverageResult {
            points = List.copyOf(Objects.requireNonNull(points, "points must not be null"));
        }
    }

    public record RollingAveragePointResult(
            LocalDate date,
            long dayNumber,
            BigDecimal averageWeightInKg,
            int includedMeasurementCount,
            boolean completeCalendarWindow) {}

    public GetWeightAnalysisResult(
            LocalDate timelineStartDate,
            DateRangeResult range,
            List<WeightMeasurementResult> weightMeasurements) {
        this(timelineStartDate, range, weightMeasurements, List.of());
    }

    public GetWeightAnalysisResult {
        weightMeasurements =
                List.copyOf(
                        Objects.requireNonNull(
                                weightMeasurements, "weight measurements must not be null"));
        rollingAverages =
                List.copyOf(
                        Objects.requireNonNull(
                                rollingAverages, "rolling averages must not be null"));
    }

    public static GetWeightAnalysisResult empty() {
        return new GetWeightAnalysisResult(null, null, List.of(), List.of());
    }
}

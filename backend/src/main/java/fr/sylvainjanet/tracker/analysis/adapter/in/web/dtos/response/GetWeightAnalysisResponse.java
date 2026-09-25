package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record GetWeightAnalysisResponse(
        LocalDate timelineStartDate,
        DateRangeResponse range,
        List<WeightMeasurementResponse> weightMeasurements,
        List<RollingAverageResponse> rollingAverages) {

    public record DateRangeResponse(LocalDate startDate, LocalDate endDate) {}

    public record WeightMeasurementResponse(
            LocalDate date, long dayNumber, BigDecimal weightInKg) {}

    public record RollingAverageResponse(
            int windowInDays, List<RollingAveragePointResponse> points) {

        public RollingAverageResponse {
            points = List.copyOf(Objects.requireNonNull(points, "points must not be null"));
        }
    }

    public record RollingAveragePointResponse(
            LocalDate date,
            long dayNumber,
            BigDecimal averageWeightInKg,
            int includedMeasurementCount,
            boolean completeCalendarWindow) {}

    public GetWeightAnalysisResponse {
        weightMeasurements =
                List.copyOf(
                        Objects.requireNonNull(
                                weightMeasurements, "weight measurements must not be null"));
        rollingAverages =
                List.copyOf(
                        Objects.requireNonNull(
                                rollingAverages, "rolling averages must not be null"));
    }
}

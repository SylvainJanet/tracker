package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record GetWeightAnalysisResult(
        LocalDate timelineStartDate,
        DateRangeResult range,
        List<WeightMeasurementResult> weightMeasurements) {

    public record DateRangeResult(LocalDate startDate, LocalDate endDate) {}

    public record WeightMeasurementResult(LocalDate date, long dayNumber, BigDecimal weightInKg) {}

    public GetWeightAnalysisResult {
        weightMeasurements =
                List.copyOf(
                        Objects.requireNonNull(
                                weightMeasurements, "weight measurements must not be null"));
    }

    public static GetWeightAnalysisResult empty() {
        return new GetWeightAnalysisResult(null, null, List.of());
    }
}

package fr.sylvainjanet.tracker.journal.application.port.in.dtos.result;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record GetWeightMeasurementInDateRangeResult(
        List<WeightMeasurementByDateResult> weightMeasurementsByDate) {
    public record WeightMeasurementByDateResult(LocalDate date, BigDecimal weightInKg) {}

    public GetWeightMeasurementInDateRangeResult {
        weightMeasurementsByDate =
                List.copyOf(
                        Objects.requireNonNull(
                                weightMeasurementsByDate,
                                "weight measurements by date must not be null"));
    }
}

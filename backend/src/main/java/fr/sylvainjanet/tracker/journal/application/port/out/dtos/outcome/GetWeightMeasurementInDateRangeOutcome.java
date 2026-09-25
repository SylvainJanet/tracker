package fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record GetWeightMeasurementInDateRangeOutcome(
        List<WeightMeasurementByDateOutcome> weightMeasurementsByDate) {
    public record WeightMeasurementByDateOutcome(LocalDate date, BigDecimal weightInKg) {}

    public GetWeightMeasurementInDateRangeOutcome {
        weightMeasurementsByDate =
                List.copyOf(
                        Objects.requireNonNull(
                                weightMeasurementsByDate,
                                "weight measurements by date must not be null"));
    }
}

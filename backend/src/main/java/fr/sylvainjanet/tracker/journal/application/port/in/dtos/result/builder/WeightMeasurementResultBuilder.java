package fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.WeightMeasurementResult;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class WeightMeasurementResultBuilder {

    private LocalDate date;
    private BigDecimal weightInKg;

    private WeightMeasurementResultBuilder() {}

    public static WeightMeasurementResultBuilder aWeightMeasurementResult() {
        return new WeightMeasurementResultBuilder();
    }

    public WeightMeasurementResultBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public WeightMeasurementResultBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public WeightMeasurementResult build() {
        return new WeightMeasurementResult(date, weightInKg);
    }
}

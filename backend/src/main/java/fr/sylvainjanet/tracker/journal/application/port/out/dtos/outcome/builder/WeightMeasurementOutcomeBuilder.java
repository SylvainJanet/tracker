package fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.builder;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.WeightMeasurementOutcome;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class WeightMeasurementOutcomeBuilder {

    private LocalDate date;
    private BigDecimal weightInKg;

    private WeightMeasurementOutcomeBuilder() {}

    public static WeightMeasurementOutcomeBuilder aWeightMeasurementOutcome() {
        return new WeightMeasurementOutcomeBuilder();
    }

    public WeightMeasurementOutcomeBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public WeightMeasurementOutcomeBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public WeightMeasurementOutcome build() {
        return new WeightMeasurementOutcome(date, weightInKg);
    }
}

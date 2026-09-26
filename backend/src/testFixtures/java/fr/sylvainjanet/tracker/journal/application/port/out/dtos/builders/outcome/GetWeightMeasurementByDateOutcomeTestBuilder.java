package fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.outcome;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementByDateOutcome;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GetWeightMeasurementByDateOutcomeTestBuilder {

    private LocalDate date;
    private BigDecimal weightInKg;

    private GetWeightMeasurementByDateOutcomeTestBuilder() {
        /* This builder should not be instantiated */
    }

    public static GetWeightMeasurementByDateOutcomeTestBuilder
            aGetWeightMeasurementByDateOutcome() {
        return new GetWeightMeasurementByDateOutcomeTestBuilder();
    }

    public GetWeightMeasurementByDateOutcomeTestBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public GetWeightMeasurementByDateOutcomeTestBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public GetWeightMeasurementByDateOutcome build() {
        return new GetWeightMeasurementByDateOutcome(date, weightInKg);
    }
}

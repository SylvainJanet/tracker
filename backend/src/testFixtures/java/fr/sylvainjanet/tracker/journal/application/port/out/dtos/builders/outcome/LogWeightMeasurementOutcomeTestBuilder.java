package fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.outcome;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.LogWeightMeasurementOutcome;
import java.math.BigDecimal;
import java.time.LocalDate;

public class LogWeightMeasurementOutcomeTestBuilder {

    private LocalDate date;
    private BigDecimal weightInKg;

    private LogWeightMeasurementOutcomeTestBuilder() {
        /* This builder should not be instantiated */
    }

    public static LogWeightMeasurementOutcomeTestBuilder aLogWeightMeasurementOutcome() {
        return new LogWeightMeasurementOutcomeTestBuilder();
    }

    public LogWeightMeasurementOutcomeTestBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LogWeightMeasurementOutcomeTestBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public LogWeightMeasurementOutcome build() {
        return new LogWeightMeasurementOutcome(date, weightInKg);
    }
}

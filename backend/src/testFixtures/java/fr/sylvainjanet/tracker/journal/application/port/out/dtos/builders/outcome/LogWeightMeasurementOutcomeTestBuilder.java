package fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.outcome;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.LogWeightMeasurementOutcome;
import java.time.LocalDate;

public class LogWeightMeasurementOutcomeTestBuilder {
    private LocalDate date;
    private Float weightInKg;

    public static LogWeightMeasurementOutcomeTestBuilder aLogWeightMeasurementOutcome() {
        return new LogWeightMeasurementOutcomeTestBuilder();
    }

    public LogWeightMeasurementOutcomeTestBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LogWeightMeasurementOutcomeTestBuilder withWeightInKg(Float weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public LogWeightMeasurementOutcome build() {
        return new LogWeightMeasurementOutcome(date, weightInKg);
    }
}

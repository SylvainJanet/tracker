package fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.builder;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetFirstWeightMeasurementDateOutcome;
import java.time.LocalDate;

public final class GetFirstWeightMeasurementDateOutcomeBuilder {

    private LocalDate date;

    private GetFirstWeightMeasurementDateOutcomeBuilder() {}

    public static GetFirstWeightMeasurementDateOutcomeBuilder
            aGetFirstWeightMeasurementDateOutcome() {
        return new GetFirstWeightMeasurementDateOutcomeBuilder();
    }

    public GetFirstWeightMeasurementDateOutcomeBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public GetFirstWeightMeasurementDateOutcome build() {
        return new GetFirstWeightMeasurementDateOutcome(date);
    }
}

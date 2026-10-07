package fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetFirstWeightMeasurementDateResult;
import java.time.LocalDate;

public final class GetFirstWeightMeasurementDateResultBuilder {

    private LocalDate date;

    private GetFirstWeightMeasurementDateResultBuilder() {}

    public static GetFirstWeightMeasurementDateResultBuilder
            aGetFirstWeightMeasurementDateResult() {
        return new GetFirstWeightMeasurementDateResultBuilder();
    }

    public GetFirstWeightMeasurementDateResultBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public GetFirstWeightMeasurementDateResult build() {
        return new GetFirstWeightMeasurementDateResult(date);
    }
}

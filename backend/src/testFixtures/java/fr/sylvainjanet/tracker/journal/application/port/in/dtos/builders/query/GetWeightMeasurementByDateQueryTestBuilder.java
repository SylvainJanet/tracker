package fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.query;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementByDateQuery;
import java.time.LocalDate;

public class GetWeightMeasurementByDateQueryTestBuilder {
    private LocalDate date;

    public static GetWeightMeasurementByDateQueryTestBuilder aGetWeightMeasurementByDateQuery() {
        return new GetWeightMeasurementByDateQueryTestBuilder();
    }

    public GetWeightMeasurementByDateQueryTestBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public GetWeightMeasurementByDateQuery build() {
        return new GetWeightMeasurementByDateQuery(date);
    }
}

package fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.builder;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementByDateQuery;
import java.time.LocalDate;

public final class GetWeightMeasurementByDateQueryBuilder {

    private LocalDate date;

    private GetWeightMeasurementByDateQueryBuilder() {}

    public static GetWeightMeasurementByDateQueryBuilder aGetWeightMeasurementByDateQuery() {
        return new GetWeightMeasurementByDateQueryBuilder();
    }

    public GetWeightMeasurementByDateQueryBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public GetWeightMeasurementByDateQuery build() {
        return new GetWeightMeasurementByDateQuery(date);
    }
}

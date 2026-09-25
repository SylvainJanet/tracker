package fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.builder;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import java.time.LocalDate;

public final class GetWeightMeasurementInDateRangeQueryBuilder {

    private LocalDate startDate;
    private LocalDate endDate;

    private GetWeightMeasurementInDateRangeQueryBuilder() {}

    public static GetWeightMeasurementInDateRangeQueryBuilder
            aGetWeightMeasurementInDateRangeQuery() {
        return new GetWeightMeasurementInDateRangeQueryBuilder();
    }

    public GetWeightMeasurementInDateRangeQueryBuilder withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public GetWeightMeasurementInDateRangeQueryBuilder withEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public GetWeightMeasurementInDateRangeQuery build() {
        return new GetWeightMeasurementInDateRangeQuery(startDate, endDate);
    }
}

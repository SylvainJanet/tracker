package fr.sylvainjanet.tracker.analysis.application.service.mapper.query;

import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.builder.GetWeightMeasurementInDateRangeQueryBuilder.aGetWeightMeasurementInDateRangeQuery;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.shared.domain.DateRange;

public final class GetWeightMeasurementInDateRangeQueryMapper {

    private GetWeightMeasurementInDateRangeQueryMapper() {}

    public static GetWeightMeasurementInDateRangeQuery query(DateRange range) {

        return aGetWeightMeasurementInDateRangeQuery()
                .withStartDate(range.startDate())
                .withEndDate(range.endDate())
                .build();
    }

    public static DateRange dateRange(GetWeightMeasurementInDateRangeQuery query) {
        return DateRange.create(query.startDate(), query.endDate());
    }
}

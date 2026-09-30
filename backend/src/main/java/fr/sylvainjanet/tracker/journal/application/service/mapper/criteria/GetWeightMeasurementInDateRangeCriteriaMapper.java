package fr.sylvainjanet.tracker.journal.application.service.mapper.criteria;

import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.builder.GetWeightMeasurementInDateRangeCriteriaBuilder.aGetWeightMeasurementInDateRangeCriteria;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementInDateRangeCriteria;
import fr.sylvainjanet.tracker.shared.domain.DateRange;

public final class GetWeightMeasurementInDateRangeCriteriaMapper {

    private GetWeightMeasurementInDateRangeCriteriaMapper() {}

    public static GetWeightMeasurementInDateRangeCriteria criteria(DateRange dateRange) {
        return aGetWeightMeasurementInDateRangeCriteria()
                .withStartDate(dateRange.startDate())
                .withEndDate(dateRange.endDate())
                .build();
    }
}

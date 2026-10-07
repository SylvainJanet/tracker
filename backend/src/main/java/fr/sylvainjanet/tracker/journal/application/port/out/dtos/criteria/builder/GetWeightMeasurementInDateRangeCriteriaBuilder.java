package fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.builder;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementInDateRangeCriteria;
import java.time.LocalDate;

public final class GetWeightMeasurementInDateRangeCriteriaBuilder {

    private LocalDate startDate;
    private LocalDate endDate;

    private GetWeightMeasurementInDateRangeCriteriaBuilder() {}

    public static GetWeightMeasurementInDateRangeCriteriaBuilder
            aGetWeightMeasurementInDateRangeCriteria() {
        return new GetWeightMeasurementInDateRangeCriteriaBuilder();
    }

    public GetWeightMeasurementInDateRangeCriteriaBuilder withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public GetWeightMeasurementInDateRangeCriteriaBuilder withEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public GetWeightMeasurementInDateRangeCriteria build() {
        return new GetWeightMeasurementInDateRangeCriteria(startDate, endDate);
    }
}

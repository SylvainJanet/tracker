package fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.builder;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementByDateCriteria;
import java.time.LocalDate;

public final class GetWeightMeasurementByDateCriteriaBuilder {

    private LocalDate date;

    private GetWeightMeasurementByDateCriteriaBuilder() {}

    public static GetWeightMeasurementByDateCriteriaBuilder aGetWeightMeasurementByDateCriteria() {
        return new GetWeightMeasurementByDateCriteriaBuilder();
    }

    public GetWeightMeasurementByDateCriteriaBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public GetWeightMeasurementByDateCriteria build() {
        return new GetWeightMeasurementByDateCriteria(date);
    }
}

package fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.criteria;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementByDateCriteria;
import java.time.LocalDate;

public class GetWeightMeasurementByDateCriteriaTestBuilder {

    private LocalDate date;

    public static GetWeightMeasurementByDateCriteriaTestBuilder
            aGetWeightMeasurementByDateCriteria() {
        return new GetWeightMeasurementByDateCriteriaTestBuilder();
    }

    public GetWeightMeasurementByDateCriteriaTestBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public GetWeightMeasurementByDateCriteria build() {
        return new GetWeightMeasurementByDateCriteria(date);
    }
}

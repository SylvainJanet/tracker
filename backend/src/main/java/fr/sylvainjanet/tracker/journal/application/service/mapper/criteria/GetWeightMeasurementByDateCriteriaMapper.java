package fr.sylvainjanet.tracker.journal.application.service.mapper.criteria;

import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.builder.GetWeightMeasurementByDateCriteriaBuilder.aGetWeightMeasurementByDateCriteria;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementByDateCriteria;
import java.time.LocalDate;

public final class GetWeightMeasurementByDateCriteriaMapper {

    private GetWeightMeasurementByDateCriteriaMapper() {}

    public static GetWeightMeasurementByDateCriteria dateCriteria(LocalDate date) {
        return aGetWeightMeasurementByDateCriteria().withDate(date).build();
    }
}

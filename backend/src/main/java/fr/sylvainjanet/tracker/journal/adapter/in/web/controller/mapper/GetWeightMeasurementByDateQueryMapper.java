package fr.sylvainjanet.tracker.journal.adapter.in.web.controller.mapper;

import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.builder.GetWeightMeasurementByDateQueryBuilder.aGetWeightMeasurementByDateQuery;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementByDateQuery;
import java.time.LocalDate;

public final class GetWeightMeasurementByDateQueryMapper {

    private GetWeightMeasurementByDateQueryMapper() {}

    public static GetWeightMeasurementByDateQuery query(LocalDate date) {
        return aGetWeightMeasurementByDateQuery().withDate(date).build();
    }
}

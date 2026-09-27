package fr.sylvainjanet.tracker.journal.application.service.mapper.query;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementByDateQuery;
import java.time.LocalDate;

public final class GetWeightMeasurementByDateQueryMapper {

    private GetWeightMeasurementByDateQueryMapper() {}

    public static LocalDate localDate(GetWeightMeasurementByDateQuery query) {
        return query.date();
    }
}

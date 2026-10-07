package fr.sylvainjanet.tracker.analysis.application.service.mapper.result;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetFirstWeightMeasurementDateResult;
import java.time.LocalDate;

public final class GetFirstWeightMeasurementDateResultMapper {

    private GetFirstWeightMeasurementDateResultMapper() {}

    public static LocalDate localDate(
            GetFirstWeightMeasurementDateResult getFirstWeightMeasurementDateResult) {
        return getFirstWeightMeasurementDateResult.date();
    }
}

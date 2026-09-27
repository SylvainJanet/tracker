package fr.sylvainjanet.tracker.journal.application.service.mapper.result;

import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.builder.GetFirstWeightMeasurementDateResultBuilder.aGetFirstWeightMeasurementDateResult;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetFirstWeightMeasurementDateResult;
import java.time.LocalDate;

public final class GetFirstWeightMeasurementDateResultMapper {

    private GetFirstWeightMeasurementDateResultMapper() {}

    public static GetFirstWeightMeasurementDateResult result(LocalDate date) {
        return aGetFirstWeightMeasurementDateResult().withDate(date).build();
    }
}

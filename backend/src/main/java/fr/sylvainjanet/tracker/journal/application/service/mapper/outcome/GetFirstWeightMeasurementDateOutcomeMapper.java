package fr.sylvainjanet.tracker.journal.application.service.mapper.outcome;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetFirstWeightMeasurementDateOutcome;
import java.time.LocalDate;

public final class GetFirstWeightMeasurementDateOutcomeMapper {

    private GetFirstWeightMeasurementDateOutcomeMapper() {}

    public static LocalDate localDate(GetFirstWeightMeasurementDateOutcome outcome) {
        return outcome.date();
    }
}

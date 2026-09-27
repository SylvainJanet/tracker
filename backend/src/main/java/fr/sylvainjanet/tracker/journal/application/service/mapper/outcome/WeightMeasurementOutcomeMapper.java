package fr.sylvainjanet.tracker.journal.application.service.mapper.outcome;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.WeightMeasurementOutcome;
import fr.sylvainjanet.tracker.journal.domain.entity.WeightMeasurement;
import fr.sylvainjanet.tracker.journal.domain.value.Weight;

public final class WeightMeasurementOutcomeMapper {

    private WeightMeasurementOutcomeMapper() {}

    public static WeightMeasurement measurement(WeightMeasurementOutcome outcome) {
        return WeightMeasurement.create(outcome.date(), Weight.create(outcome.weightInKg()));
    }
}

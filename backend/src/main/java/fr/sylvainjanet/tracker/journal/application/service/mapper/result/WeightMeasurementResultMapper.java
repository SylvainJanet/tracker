package fr.sylvainjanet.tracker.journal.application.service.mapper.result;

import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.builder.WeightMeasurementResultBuilder.aWeightMeasurementResult;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.WeightMeasurementResult;
import fr.sylvainjanet.tracker.journal.domain.entity.WeightMeasurement;

public final class WeightMeasurementResultMapper {

    private WeightMeasurementResultMapper() {}

    public static WeightMeasurementResult result(WeightMeasurement weightMeasurement) {
        return aWeightMeasurementResult()
                .withDate(weightMeasurement.date())
                .withWeightInKg(weightMeasurement.weightInKilograms())
                .build();
    }
}

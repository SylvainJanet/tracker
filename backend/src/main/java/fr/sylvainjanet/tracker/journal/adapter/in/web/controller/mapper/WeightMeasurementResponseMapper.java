package fr.sylvainjanet.tracker.journal.adapter.in.web.controller.mapper;

import static fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.response.builder.WeightMeasurementResponseBuilder.aWeightMeasurementResponse;

import fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.response.WeightMeasurementResponse;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.WeightMeasurementResult;

public final class WeightMeasurementResponseMapper {

    private WeightMeasurementResponseMapper() {}

    public static WeightMeasurementResponse response(WeightMeasurementResult result) {

        return aWeightMeasurementResponse()
                .withDate(result.date())
                .withWeightInKg(result.weightInKg())
                .build();
    }
}

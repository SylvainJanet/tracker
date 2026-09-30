package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller.mapper;

import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder.AnalysisValueResponseBuilder.anAnalysisValueResponse;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisValueResponse;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisValueResult;

public final class AnalysisValueResponseMapper {

    private AnalysisValueResponseMapper() {}

    public static AnalysisValueResponse analysisValue(AnalysisValueResult measurement) {

        return anAnalysisValueResponse()
                .withDate(measurement.date())
                .withDayNumber(measurement.dayNumber())
                .withWeightInKg(measurement.weightInKg())
                .build();
    }
}

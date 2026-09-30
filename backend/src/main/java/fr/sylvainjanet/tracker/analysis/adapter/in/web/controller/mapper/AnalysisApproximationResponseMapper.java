package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller.mapper;

import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder.AnalysisApproximationResponseBuilder.anAnalysisApproximationResponse;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisApproximationResponse;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisApproximationResult;

public final class AnalysisApproximationResponseMapper {

    private AnalysisApproximationResponseMapper() {}

    public static AnalysisApproximationResponse approximation(
            AnalysisApproximationResult approximation) {
        return anAnalysisApproximationResponse()
                .withValue(approximation.value())
                .withRounding(AnalysisRoundingResponseMapper.rounding(approximation.rounding()))
                .build();
    }
}

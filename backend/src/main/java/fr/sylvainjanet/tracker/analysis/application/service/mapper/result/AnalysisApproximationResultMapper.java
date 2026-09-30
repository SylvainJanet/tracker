package fr.sylvainjanet.tracker.analysis.application.service.mapper.result;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.AnalysisApproximationResultBuilder.anAnalysisApproximationResult;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisApproximationResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ApproximationResult;

public final class AnalysisApproximationResultMapper {

    private AnalysisApproximationResultMapper() {}

    public static AnalysisApproximationResult approximation(ApproximationResult approximation) {
        return anAnalysisApproximationResult()
                .withValue(approximation.value())
                .withRounding(AnalysisRoundingResultMapper.rounding(approximation.rounding()))
                .build();
    }
}

package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller.mapper;

import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder.AnalysisRollingAverageValueResponseBuilder.anAnalysisRollingAverageValueResponse;
import static java.util.Comparator.comparingInt;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisApproximationResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisExactValueResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRollingAverageValueResponse;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisFractionResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageValueResult;
import java.util.List;

public final class AnalysisRollingAverageValueResponseMapper {

    private AnalysisRollingAverageValueResponseMapper() {}

    public static AnalysisRollingAverageValueResponse rollingAverage(
            AnalysisRollingAverageValueResult result) {
        AnalysisExactValueResponse exactValue =
                switch (result.exactValue()) {
                    case AnalysisFractionResult fractionResult ->
                            AnalysisFractionResponseMapper.fraction(fractionResult);
                };

        List<AnalysisApproximationResponse> approximations =
                result.approximations().stream()
                        .map(AnalysisApproximationResponseMapper::approximation)
                        .sorted(comparingInt(AnalysisApproximationResponse::displayOrder))
                        .toList();

        return anAnalysisRollingAverageValueResponse()
                .withExactValue(exactValue)
                .withApproximations(approximations)
                .build();
    }
}

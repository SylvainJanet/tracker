package fr.sylvainjanet.tracker.analysis.application.service.mapper.result;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.AnalysisRollingAverageValueResultBuilder.anAnalysisRollingAverageValueResult;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisApproximationResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisExactValueResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageValueResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.FractionResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ValueResult;
import java.util.Set;
import java.util.stream.Collectors;

public final class AnalysisRollingAverageValueResultMapper {

    private AnalysisRollingAverageValueResultMapper() {}

    public static AnalysisRollingAverageValueResult rollingAverage(ValueResult valueResult) {
        AnalysisExactValueResult exactValue =
                switch (valueResult.exactValue()) {
                    case FractionResult fractionResult ->
                            AnalysisFractionResultMapper.fraction(fractionResult);
                };

        Set<AnalysisApproximationResult> approximations =
                valueResult.approximations().stream()
                        .map(AnalysisApproximationResultMapper::approximation)
                        .collect(Collectors.toSet());

        return anAnalysisRollingAverageValueResult()
                .withExactValue(exactValue)
                .withApproximations(approximations)
                .build();
    }
}

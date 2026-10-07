package fr.sylvainjanet.tracker.analysis.application.service.mapper.result;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.AnalysisFractionResultBuilder.anAnalysisFractionResult;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisExactValueResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.FractionResult;

public final class AnalysisFractionResultMapper {

    private AnalysisFractionResultMapper() {}

    public static AnalysisExactValueResult fraction(FractionResult fractionResult) {
        return anAnalysisFractionResult()
                .withNumerator(fractionResult.numerator())
                .withDenominator(fractionResult.denominator())
                .build();
    }
}

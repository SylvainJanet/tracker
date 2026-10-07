package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller.mapper;

import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder.AnalysisFractionResponseBuilder.anAnalysisFractionResponse;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisExactValueResponse;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisFractionResult;

public final class AnalysisFractionResponseMapper {

    private AnalysisFractionResponseMapper() {}

    public static AnalysisExactValueResponse fraction(AnalysisFractionResult fractionResponse) {
        return anAnalysisFractionResponse()
                .withNumerator(fractionResponse.numerator())
                .withDenominator(fractionResponse.denominator())
                .build();
    }
}

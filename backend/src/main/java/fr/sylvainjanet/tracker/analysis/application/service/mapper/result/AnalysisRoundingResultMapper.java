package fr.sylvainjanet.tracker.analysis.application.service.mapper.result;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRoundingResult.PRECISE;
import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRoundingResult.PRETTY;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRoundingResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculationRoundingResult;

public final class AnalysisRoundingResultMapper {

    private AnalysisRoundingResultMapper() {}

    public static AnalysisRoundingResult rounding(CalculationRoundingResult rounding) {

        return switch (rounding) {
            case PRECISE -> PRECISE;
            case PRETTY -> PRETTY;
        };
    }
}

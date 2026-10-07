package fr.sylvainjanet.tracker.statistics.application.service.mapper.result;

import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculationRoundingResult.PRECISE;
import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculationRoundingResult.PRETTY;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculationRoundingResult;
import fr.sylvainjanet.tracker.statistics.domain.value.CalculationRounding;

public final class CalculationRoundingResultMapper {

    private CalculationRoundingResultMapper() {}

    public static CalculationRoundingResult calculationRoundingResult(
            CalculationRounding rounding) {
        return switch (rounding) {
            case PRECISE -> PRECISE;
            case PRETTY -> PRETTY;
        };
    }
}

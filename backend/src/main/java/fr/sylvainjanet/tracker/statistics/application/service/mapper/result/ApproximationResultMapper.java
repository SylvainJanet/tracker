package fr.sylvainjanet.tracker.statistics.application.service.mapper.result;

import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder.ApproximationResultBuilder.anApproximationResult;
import static fr.sylvainjanet.tracker.statistics.application.service.mapper.result.CalculationRoundingResultMapper.calculationRoundingResult;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ApproximationResult;
import fr.sylvainjanet.tracker.statistics.domain.value.Approximation;

public final class ApproximationResultMapper {

    private ApproximationResultMapper() {}

    public static ApproximationResult approximationResult(Approximation approximation) {
        return anApproximationResult()
                .withValue(approximation.value())
                .withRounding(calculationRoundingResult(approximation.rounding()))
                .build();
    }
}

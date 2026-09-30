package fr.sylvainjanet.tracker.statistics.application.service.mapper.result;

import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder.FractionResultBuilder.aFractionResult;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.FractionResult;
import fr.sylvainjanet.tracker.statistics.domain.value.Fraction;

public final class FractionResultMapper {

    private FractionResultMapper() {}

    public static FractionResult fractionResult(Fraction fraction) {
        return aFractionResult()
                .withNumerator(fraction.numerator())
                .withDenominator(fraction.denominator())
                .build();
    }
}

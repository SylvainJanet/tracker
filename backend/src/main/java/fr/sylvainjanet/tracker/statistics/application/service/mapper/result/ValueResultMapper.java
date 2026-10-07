package fr.sylvainjanet.tracker.statistics.application.service.mapper.result;

import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder.ValueResultBuilder.aValueResult;
import static fr.sylvainjanet.tracker.statistics.application.service.mapper.result.FractionResultMapper.fractionResult;
import static java.util.stream.Collectors.toSet;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ExactValueResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ValueResult;
import fr.sylvainjanet.tracker.statistics.domain.value.Fraction;
import fr.sylvainjanet.tracker.statistics.domain.value.Value;

public final class ValueResultMapper {

    private ValueResultMapper() {}

    public static ValueResult valueResult(Value value) {

        ExactValueResult exactValueResult =
                switch (value.exactValue()) {
                    case Fraction fraction -> fractionResult(fraction);
                };

        return aValueResult()
                .withExactValue(exactValueResult)
                .withApproximations(
                        value.approximations().stream()
                                .map(ApproximationResultMapper::approximationResult)
                                .collect(toSet()))
                .build();
    }
}

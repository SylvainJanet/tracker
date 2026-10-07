package fr.sylvainjanet.tracker.statistics.application.service.mapper.result;

import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder.RollingAveragePointResultBuilder.aRollingAveragePointResultBuilder;
import static fr.sylvainjanet.tracker.statistics.application.service.mapper.result.ValueResultMapper.valueResult;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.RollingAveragePointResult;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingAveragePoint;

public final class RollingAveragePointResultMapper {

    private RollingAveragePointResultMapper() {}

    public static RollingAveragePointResult rollingAveragePointResult(
            RollingAveragePoint rollingAveragePoint) {
        return aRollingAveragePointResultBuilder()
                .withIndex(rollingAveragePoint.index())
                .withIncludedIndexes(rollingAveragePoint.includedIndexes())
                .withRollingAverage(valueResult(rollingAveragePoint.rollingAverage()))
                .build();
    }
}

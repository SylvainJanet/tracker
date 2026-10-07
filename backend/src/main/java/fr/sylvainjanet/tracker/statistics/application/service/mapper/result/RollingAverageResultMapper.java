package fr.sylvainjanet.tracker.statistics.application.service.mapper.result;

import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder.RollingAverageResultBuilder.aRollingAverageResult;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.RollingAverageResult;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingAverage;

public final class RollingAverageResultMapper {

    private RollingAverageResultMapper() {}

    public static RollingAverageResult rollingAverageResult(RollingAverage rollingAverage) {
        return aRollingAverageResult()
                .withWindowSize(rollingAverage.windowSize())
                .withPoints(
                        rollingAverage.points().stream()
                                .map(RollingAveragePointResultMapper::rollingAveragePointResult)
                                .toList())
                .build();
    }
}

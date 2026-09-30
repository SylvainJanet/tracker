package fr.sylvainjanet.tracker.statistics.application.service.mapper.result;

import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder.CalculateRollingAveragesResultBuilder.aCalculateRollingAveragesResult;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingAverage;
import java.util.List;

public final class CalculateRollingAveragesResultMapper {

    private CalculateRollingAveragesResultMapper() {}

    public static CalculateRollingAveragesResult calculateRollingAveragesResult(
            List<RollingAverage> rollingAverages) {
        return aCalculateRollingAveragesResult()
                .withRollingAverages(
                        rollingAverages.stream()
                                .map(RollingAverageResultMapper::rollingAverageResult)
                                .toList())
                .build();
    }
}

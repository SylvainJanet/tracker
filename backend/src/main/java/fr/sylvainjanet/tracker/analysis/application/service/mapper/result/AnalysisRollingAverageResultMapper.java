package fr.sylvainjanet.tracker.analysis.application.service.mapper.result;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.AnalysisRollingAverageResultBuilder.anAnalysisRollingAverageResult;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAveragePointResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisValueResult;
import fr.sylvainjanet.tracker.analysis.domain.value.DatedSeries;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.RollingAverageResult;
import java.util.List;

public final class AnalysisRollingAverageResultMapper {

    private AnalysisRollingAverageResultMapper() {}

    public static AnalysisRollingAverageResult rollingAverage(
            RollingAverageResult rollingAverageResult,
            DatedSeries series,
            List<AnalysisValueResult> weightValues) {

        long windowSize = rollingAverageResult.windowSize();
        List<AnalysisRollingAveragePointResult> rollingAverages =
                rollingAverageResult.points().stream()
                        .map(
                                point ->
                                        AnalysisRollingAveragePointResultMapper.pointResult(
                                                point, series, weightValues))
                        .toList();

        return anAnalysisRollingAverageResult()
                .withWindowSize(windowSize)
                .withRollingAverages(rollingAverages)
                .build();
    }
}

package fr.sylvainjanet.tracker.analysis.application.service.mapper.result;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.AnalysisRollingAveragePointResultBuilder.anAnalysisRollingAveragePointResult;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAveragePointResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageValueResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisValueResult;
import fr.sylvainjanet.tracker.analysis.domain.value.Data;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.RollingAveragePointResult;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class AnalysisRollingAveragePointResultMapper {

    private AnalysisRollingAveragePointResultMapper() {}

    public static AnalysisRollingAveragePointResult pointResult(
            RollingAveragePointResult point, Data data, List<AnalysisValueResult> weightValues) {

        LocalDate date = data.dateForIndex(point.index());
        long dayNumber = point.index();
        Set<Long> includedIndexes = point.includedIndexes();
        AnalysisRollingAverageValueResult rollingAverage =
                AnalysisRollingAverageValueResultMapper.rollingAverage(point.rollingAverage());
        Set<AnalysisValueResult> includedValues =
                weightValues.stream()
                        .filter(value -> includedIndexes.contains(value.dayNumber()))
                        .collect(Collectors.toSet());

        return anAnalysisRollingAveragePointResult()
                .withDate(date)
                .withDayNumber(dayNumber)
                .withIncludedValues(includedValues)
                .withRollingAverage(rollingAverage)
                .build();
    }
}

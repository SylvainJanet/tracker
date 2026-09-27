package fr.sylvainjanet.tracker.analysis.application.service.mapper.result;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.aGetWeightAnalysisResult;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisDateRangeResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisValueResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.domain.value.DatedSeries;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import java.time.LocalDate;
import java.util.List;

public final class GetWeightAnalysisResultMapper {

    private GetWeightAnalysisResultMapper() {}

    public static GetWeightAnalysisResult analysisResult(
            DatedSeries series, CalculateRollingAveragesResult statisticsResult) {

        LocalDate timelineStartDate = series.timelineStartDate();
        AnalysisDateRangeResult dateRange = AnalysisDateRangeResultMapper.dateRange(series.range());
        List<AnalysisValueResult> weightValues =
                series.values().stream()
                        .map(value -> AnalysisValueResultMapper.analysisValue(series, value))
                        .toList();
        List<AnalysisRollingAverageResult> rollingAverageSeries =
                statisticsResult.rollingAverages().stream()
                        .map(
                                rollingAverageResult ->
                                        AnalysisRollingAverageResultMapper.rollingAverage(
                                                rollingAverageResult, series, weightValues))
                        .toList();
        return aGetWeightAnalysisResult()
                .withTimelineStartDate(timelineStartDate)
                .withDateRange(dateRange)
                .withWeightValues(weightValues)
                .withRollingAverageSeries(rollingAverageSeries)
                .build();
    }
}

package fr.sylvainjanet.tracker.analysis.application.service.mapper.result;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.aGetWeightAnalysisResult;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisDateRangeResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisValueResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.domain.value.Data;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import java.time.LocalDate;
import java.util.List;

public final class GetWeightAnalysisResultMapper {

    private GetWeightAnalysisResultMapper() {}

    public static GetWeightAnalysisResult analysisResult(
            Data data, CalculateRollingAveragesResult statisticsResult) {

        LocalDate timelineStartDate = data.timelineStartDate();
        AnalysisDateRangeResult dateRange =
                AnalysisDateRangeResultMapper.dateRange(data.analysisRange());
        List<AnalysisValueResult> weightValues =
                data.dataSeries().stream()
                        .map(value -> AnalysisValueResultMapper.analysisValue(data, value))
                        .toList();
        List<AnalysisRollingAverageResult> rollingAverageSeries =
                statisticsResult.rollingAverages().stream()
                        .map(
                                rollingAverageResult ->
                                        AnalysisRollingAverageResultMapper.rollingAverage(
                                                rollingAverageResult, data, weightValues))
                        .toList();
        return aGetWeightAnalysisResult()
                .withTimelineStartDate(timelineStartDate)
                .withDateRange(dateRange)
                .withWeightValues(weightValues)
                .withRollingAverageSeries(rollingAverageSeries)
                .build();
    }
}

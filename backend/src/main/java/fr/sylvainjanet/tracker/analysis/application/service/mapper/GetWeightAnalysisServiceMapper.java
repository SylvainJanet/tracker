package fr.sylvainjanet.tracker.analysis.application.service.mapper;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.AnalysisDateRangeResultBuilder.anAnalysisDateRangeResult;
import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.AnalysisWeightValueResultBuilder.anAnalysisWeightValueResult;
import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.WeightRollingAveragePointResultBuilder.aWeightRollingAveragePointResult;
import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.aGetWeightAnalysisResult;
import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.builder.GetWeightMeasurementInDateRangeQueryBuilder.aGetWeightMeasurementInDateRangeQuery;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.AnalysisWeightRollingAverageResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.AnalysisWeightValueResult;
import fr.sylvainjanet.tracker.analysis.domain.DatedSeries;
import fr.sylvainjanet.tracker.analysis.domain.DatedValue;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult.WeightMeasurementByDateResult;
import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand.IndexedValueCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public final class GetWeightAnalysisServiceMapper {

    private GetWeightAnalysisServiceMapper() {}

    public static GetWeightMeasurementInDateRangeQuery dateRangeToWeightMeasurementQuery(
            DateRange range) {
        Objects.requireNonNull(range, "date range must not be null");
        return aGetWeightMeasurementInDateRangeQuery()
                .withStartDate(range.getStartDate())
                .withEndDate(range.getEndDate())
                .build();
    }

    public static List<DatedValue> weightMeasurementsToDatedValues(
            GetWeightMeasurementInDateRangeResult result) {
        Objects.requireNonNull(result, "weight measurement result must not be null");
        return result.weightMeasurementsByDate().stream()
                .map(GetWeightAnalysisServiceMapper::weightMeasurementToDatedValue)
                .toList();
    }

    private static DatedValue weightMeasurementToDatedValue(
            WeightMeasurementByDateResult measurement) {
        return new DatedValue(measurement.date(), measurement.weightInKg());
    }

    public static GetWeightAnalysisResult statisticsToAnalysis(
            DatedSeries series, CalculateRollingAveragesResult statisticsResult) {
        Objects.requireNonNull(series, "series must not be null");
        Objects.requireNonNull(statisticsResult, "statistics result must not be null");
        List<AnalysisWeightValueResult> measurements =
                series.values().stream()
                        .map(value -> valueToResult(value, series.indexFor(value.date())))
                        .toList();

        List<AnalysisWeightRollingAverageResult> rollingAverages =
                statisticsResult.rollingAverages().stream()
                        .map(avg -> statisticsRollingAverageToAnalysis(series, avg))
                        .toList();

        return aGetWeightAnalysisResult()
                .withTimelineStartDate(series.timelineStartDate())
                .withDateRange(
                        anAnalysisDateRangeResult()
                                .withStartDate(series.range().getStartDate())
                                .withEndDate(series.range().getEndDate())
                                .build())
                .withWeightValues(measurements)
                .withRollingAverages(rollingAverages)
                .build();
    }

    private static AnalysisWeightRollingAverageResult statisticsRollingAverageToAnalysis(
            DatedSeries series, CalculateRollingAveragesResult.RollingAverageResult avg) {
        return new AnalysisWeightRollingAverageResult(
                avg.windowSize(),
                avg.points().stream()
                        .map(point -> statisticsRollingAveragePointToAnalysis(series, point))
                        .toList());
    }

    private static GetWeightAnalysisResult.@NonNull WeightRollingAveragePointResult
            statisticsRollingAveragePointToAnalysis(
                    DatedSeries series,
                    CalculateRollingAveragesResult.RollingAveragePointResult point) {
        return aWeightRollingAveragePointResult()
                .withDate(series.dateForIndex(point.index()))
                .withDayNumber(point.index())
                .withAverageWeightInKg(point.average())
                .build();
    }

    private static AnalysisWeightValueResult valueToResult(DatedValue value, long index) {
        return anAnalysisWeightValueResult()
                .withDate(value.date())
                .withDayNumber(index)
                .withWeightInKg(value.value())
                .build();
    }

    public static CalculateRollingAveragesCommand analysisToStatisticsCommand(
            DatedSeries series, List<Integer> windowSizes) {
        Objects.requireNonNull(series, "series must not be null");
        Objects.requireNonNull(windowSizes, "window sizes must not be null");
        List<IndexedValueCommand> values =
                series.values().stream()
                        .map(value -> createIndexedValueCommand(series, value))
                        .toList();

        return new CalculateRollingAveragesCommand(values, windowSizes);
    }

    private static @NonNull IndexedValueCommand createIndexedValueCommand(
            DatedSeries series, DatedValue value) {
        return new IndexedValueCommand(series.indexFor(value.date()), value.value());
    }
}

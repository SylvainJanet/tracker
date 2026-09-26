package fr.sylvainjanet.tracker.analysis.application.service.mapper;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.DateRangeResultBuilder.aDateRangeResult;
import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.WeightMeasurementResultBuilder.aWeightMeasurementResult;
import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.aGetWeightAnalysisResult;
import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.builder.GetWeightMeasurementInDateRangeQueryBuilder.aGetWeightMeasurementInDateRangeQuery;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.WeightMeasurementResult;
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

public final class GetWeightAnalysisServiceMapper {

    private GetWeightAnalysisServiceMapper() {}

    public static GetWeightMeasurementInDateRangeQuery dateRangeToQuery(DateRange range) {
        return aGetWeightMeasurementInDateRangeQuery()
                .withStartDate(range.getStartDate())
                .withEndDate(range.getEndDate())
                .build();
    }

    public static List<DatedValue> measurementsToValues(
            GetWeightMeasurementInDateRangeResult result) {
        return result.weightMeasurementsByDate().stream()
                .map(GetWeightAnalysisServiceMapper::measurementToValue)
                .toList();
    }

    private static DatedValue measurementToValue(WeightMeasurementByDateResult measurement) {
        return new DatedValue(measurement.date(), measurement.weightInKg());
    }

    public static GetWeightAnalysisResult analysisToResult(DatedSeries series) {
        List<WeightMeasurementResult> measurements =
                series.values().stream()
                        .map(value -> valueToResult(value, series.indexFor(value.date())))
                        .toList();

        return aGetWeightAnalysisResult()
                .withTimelineStartDate(series.timelineStartDate())
                .withRange(
                        aDateRangeResult()
                                .withStartDate(series.range().getStartDate())
                                .withEndDate(series.range().getEndDate())
                                .build())
                .withWeightMeasurements(measurements)
                .build();
    }

    private static WeightMeasurementResult valueToResult(DatedValue value, long index) {
        return aWeightMeasurementResult()
                .withDate(value.date())
                .withDayNumber(index)
                .withWeightInKg(value.value())
                .build();
    }

    public static CalculateRollingAveragesCommand analysisToStatisticsCommand(DatedSeries series) {
        List<IndexedValueCommand> values =
                series.values().stream()
                        .map(
                                value ->
                                        new IndexedValueCommand(
                                                series.indexFor(value.date()), value.value()))
                        .toList();

        return new CalculateRollingAveragesCommand(values, List.of());
    }

    public static GetWeightAnalysisResult analysisToResult(
            DatedSeries series, CalculateRollingAveragesResult statisticsResult) {
        Objects.requireNonNull(statisticsResult, "statistics result must not be null");

        GetWeightAnalysisResult analysisResult = analysisToResult(series);

        return new GetWeightAnalysisResult(
                analysisResult.timelineStartDate(),
                analysisResult.range(),
                analysisResult.weightMeasurements(),
                List.of());
    }
}

package fr.sylvainjanet.tracker.analysis.application.service.mapper;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.DateRangeResultBuilder.aDateRangeResult;
import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.WeightMeasurementResultBuilder.aWeightMeasurementResult;
import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.aGetWeightAnalysisResult;
import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.builder.GetWeightMeasurementInDateRangeQueryBuilder.aGetWeightMeasurementInDateRangeQuery;
import static fr.sylvainjanet.tracker.shared.domain.builder.WeightMeasurementBuilder.aWeightMeasurement;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.WeightMeasurementResult;
import fr.sylvainjanet.tracker.analysis.domain.WeightAnalysis;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult.WeightMeasurementByDateResult;
import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.shared.domain.WeightMeasurement;
import java.util.List;

public final class GetWeightAnalysisServiceMapper {

    private GetWeightAnalysisServiceMapper() {}

    public static GetWeightMeasurementInDateRangeQuery dateRangeToQuery(DateRange range) {
        return aGetWeightMeasurementInDateRangeQuery()
                .withStartDate(range.getStartDate())
                .withEndDate(range.getEndDate())
                .build();
    }

    public static List<WeightMeasurement> measurementsToDomain(
            GetWeightMeasurementInDateRangeResult result) {
        return result.weightMeasurementsByDate().stream()
                .map(GetWeightAnalysisServiceMapper::measurementToDomain)
                .toList();
    }

    private static WeightMeasurement measurementToDomain(
            WeightMeasurementByDateResult measurement) {
        return aWeightMeasurement()
                .withDate(measurement.date())
                .withWeightInKg(measurement.weightInKg())
                .build();
    }

    public static GetWeightAnalysisResult analysisToResult(WeightAnalysis analysis) {
        List<WeightMeasurementResult> measurements =
                analysis.weightMeasurements().stream()
                        .map(
                                measurement ->
                                        measurementToResult(
                                                measurement,
                                                analysis.dayNumberFor(measurement.date())))
                        .toList();

        return aGetWeightAnalysisResult()
                .withTimelineStartDate(analysis.timelineStartDate())
                .withRange(
                        aDateRangeResult()
                                .withStartDate(analysis.range().getStartDate())
                                .withEndDate(analysis.range().getEndDate())
                                .build())
                .withWeightMeasurements(measurements)
                .build();
    }

    private static WeightMeasurementResult measurementToResult(
            WeightMeasurement measurement, long dayNumber) {
        return aWeightMeasurementResult()
                .withDate(measurement.date())
                .withDayNumber(dayNumber)
                .withWeightInKg(measurement.weightInKilograms())
                .build();
    }
}

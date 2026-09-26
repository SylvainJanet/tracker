package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.mapper;

import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder.GetWeightAnalysisResponseBuilder.DateRangeResponseBuilder.aDateRangeResponse;
import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder.GetWeightAnalysisResponseBuilder.WeightMeasurementResponseBuilder.aWeightMeasurementResponse;
import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder.GetWeightAnalysisResponseBuilder.aGetWeightAnalysisResponse;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse.DateRangeResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse.WeightMeasurementResponse;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.DateRangeResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.WeightMeasurementResult;
import java.util.List;

public final class GetWeightAnalysisResponseMapper {

    private GetWeightAnalysisResponseMapper() {}

    public static GetWeightAnalysisResponse resultToResponse(GetWeightAnalysisResult result) {
        List<WeightMeasurementResponse> measurements =
                result.weightMeasurements().stream()
                        .map(GetWeightAnalysisResponseMapper::measurementToResponse)
                        .toList();

        return aGetWeightAnalysisResponse()
                .withTimelineStartDate(result.timelineStartDate())
                .withRange(dateRangeToResponse(result.range()))
                .withWeightMeasurements(measurements)
                .withRollingAverages(List.of())
                .build();
    }

    private static WeightMeasurementResponse measurementToResponse(
            WeightMeasurementResult measurement) {
        return aWeightMeasurementResponse()
                .withDate(measurement.date())
                .withDayNumber(measurement.dayNumber())
                .withWeightInKg(measurement.weightInKg())
                .build();
    }

    private static DateRangeResponse dateRangeToResponse(DateRangeResult range) {
        if (range == null) {
            return null;
        }

        return aDateRangeResponse()
                .withStartDate(range.startDate())
                .withEndDate(range.endDate())
                .build();
    }
}

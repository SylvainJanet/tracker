package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller.mapper;

import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder.GetWeightAnalysisResponseBuilder.aGetWeightAnalysisResponse;
import static java.util.Comparator.comparingLong;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisDateRangeResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRollingAverageResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisValueResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public final class GetWeightAnalysisResponseMapper {

    private GetWeightAnalysisResponseMapper() {}

    public static GetWeightAnalysisResponse resultToResponse(GetWeightAnalysisResult result) {

        LocalDate timelineStartDate = result.timelineStartDate();
        AnalysisDateRangeResponse range =
                result.dateRange() == null
                        ? null
                        : AnalysisDateRangeResponseMapper.dateRange(result.dateRange());
        List<AnalysisValueResponse> weightMeasurements =
                result.weightValues().stream()
                        .map(AnalysisValueResponseMapper::analysisValue)
                        .sorted(Comparator.comparing(AnalysisValueResponse::dayNumber))
                        .toList();
        List<AnalysisRollingAverageResponse> rollingAverageSeries =
                result.rollingAverageSeries().stream()
                        .map(AnalysisRollingAverageResponseMapper::rollingAverage)
                        .sorted(comparingLong(AnalysisRollingAverageResponse::windowSize))
                        .toList();

        return aGetWeightAnalysisResponse()
                .withTimelineStartDate(timelineStartDate)
                .withRange(range)
                .withWeightMeasurements(weightMeasurements)
                .withRollingAverageSeries(rollingAverageSeries)
                .build();
    }
}

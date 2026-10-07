package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller.mapper;

import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder.AnalysisRollingAverageResponseBuilder.anAnalysisRollingAverageResponse;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRollingAveragePointResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRollingAverageResponse;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageResult;
import java.util.Comparator;
import java.util.List;

public final class AnalysisRollingAverageResponseMapper {

    private AnalysisRollingAverageResponseMapper() {}

    public static AnalysisRollingAverageResponse rollingAverage(
            AnalysisRollingAverageResult result) {

        long windowSize = result.windowSize();
        List<AnalysisRollingAveragePointResponse> rollingAverages =
                result.rollingAverages().stream()
                        .map(AnalysisRollingAveragePointResponseMapper::pointResponse)
                        .sorted(
                                Comparator.comparing(
                                        AnalysisRollingAveragePointResponse::dayNumber))
                        .toList();

        return anAnalysisRollingAverageResponse()
                .withWindowSize(windowSize)
                .withRollingAverages(rollingAverages)
                .build();
    }
}

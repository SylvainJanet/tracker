package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller.mapper;

import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder.AnalysisRollingAveragePointResponseBuilder.anAnalysisRollingAveragePointResponse;
import static java.util.Comparator.comparingLong;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRollingAveragePointResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRollingAverageValueResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisValueResponse;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAveragePointResult;
import java.time.LocalDate;
import java.util.List;

public final class AnalysisRollingAveragePointResponseMapper {

    private AnalysisRollingAveragePointResponseMapper() {}

    public static AnalysisRollingAveragePointResponse pointResponse(
            AnalysisRollingAveragePointResult point) {

        LocalDate date = point.date();
        long dayNumber = point.dayNumber();
        List<AnalysisValueResponse> includedValues =
                point.includedValues().stream()
                        .map(AnalysisValueResponseMapper::analysisValue)
                        .sorted(comparingLong(AnalysisValueResponse::dayNumber))
                        .toList();
        AnalysisRollingAverageValueResponse rollingAverage =
                AnalysisRollingAverageValueResponseMapper.rollingAverage(point.rollingAverage());

        return anAnalysisRollingAveragePointResponse()
                .withDate(date)
                .withDayNumber(dayNumber)
                .withIncludedValues(includedValues)
                .withRollingAverage(rollingAverage)
                .build();
    }
}

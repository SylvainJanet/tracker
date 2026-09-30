package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record AnalysisRollingAveragePointResponse(
        LocalDate date,
        @Schema(description = "One-based calendar-day index from the timeline start", minimum = "1")
                long dayNumber,
        @Schema(
                        description =
                                "Recorded measurements included in the trailing window, ordered by ascending date")
                List<AnalysisValueResponse> includedValues,
        AnalysisRollingAverageValueResponse rollingAverage) {}

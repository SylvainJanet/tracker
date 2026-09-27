package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record GetWeightAnalysisResponse(
        @Schema(
                        description =
                                "Stable timeline origin; null when no measurement is available",
                        nullable = true)
                LocalDate timelineStartDate,
        @Schema(
                        description =
                                "Inclusive represented range; null when no measurement is available",
                        nullable = true)
                AnalysisDateRangeResponse range,
        @Schema(description = "Recorded measurements ordered by ascending date")
                List<AnalysisValueResponse> weightMeasurements,
        @Schema(description = "Rolling-average series ordered by ascending window size")
                List<AnalysisRollingAverageResponse> rollingAverageSeries) {}

package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record AnalysisRollingAverageResponse(
        @Schema(description = "Trailing window size in calendar days", minimum = "1", example = "7")
                long windowSize,
        @Schema(description = "Rolling-average points ordered by ascending date")
                List<AnalysisRollingAveragePointResponse> rollingAverages) {}

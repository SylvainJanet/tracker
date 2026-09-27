package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record AnalysisRollingAverageValueResponse(
        @Schema(description = "Authoritative exact value, currently represented as a fraction")
                AnalysisExactValueResponse exactValue,
        @Schema(description = "Rounded values ordered PRETTY, then PRECISE")
                List<AnalysisApproximationResponse> approximations) {}

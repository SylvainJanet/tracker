package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record AnalysisFractionResponse(
        @Schema(description = "Exact sum of the included weights in kilograms", example = "164.00")
                BigDecimal numerator,
        @Schema(description = "Number of included measurements", minimum = "1", example = "2")
                BigDecimal denominator)
        implements AnalysisExactValueResponse {}

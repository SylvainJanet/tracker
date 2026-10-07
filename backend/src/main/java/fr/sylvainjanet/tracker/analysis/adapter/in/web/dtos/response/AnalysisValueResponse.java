package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AnalysisValueResponse(
        LocalDate date,
        @Schema(description = "One-based calendar-day index from the timeline start", minimum = "1")
                long dayNumber,
        BigDecimal weightInKg) {}

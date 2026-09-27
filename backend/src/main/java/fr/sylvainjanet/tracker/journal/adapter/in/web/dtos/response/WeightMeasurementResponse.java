package fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

public record WeightMeasurementResponse(
        LocalDate date,
        @Schema(minimum = "0", exclusiveMinimum = true, multipleOf = 0.05) BigDecimal weightInKg) {}

package fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.request;

import fr.sylvainjanet.tracker.journal.adapter.in.web.validator.annotation.WeightValid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LogWeightMeasurementRequest(
        @NotNull LocalDate date,
        @NotNull
                @WeightValid
                @Schema(
                        description = "Positive weight in kilograms, in 50-gram increments",
                        minimum = "0",
                        exclusiveMinimum = true,
                        multipleOf = 0.05)
                BigDecimal weightInKg) {}

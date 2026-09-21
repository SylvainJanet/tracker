package fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.request;

import fr.sylvainjanet.tracker.journal.adapter.in.web.validator.annotation.WeightValid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LogWeightMeasurementRequest(
        @NotNull LocalDate date, @NotNull @WeightValid BigDecimal weightInKg) {}

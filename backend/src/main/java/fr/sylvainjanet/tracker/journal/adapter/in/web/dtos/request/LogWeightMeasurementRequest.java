package fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LogWeightMeasurementRequest(
        @NotNull LocalDate date, @NotNull @Positive BigDecimal weightInKg) {}

package fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LogWeightMeasurementResponse(LocalDate date, BigDecimal weightInKg) {}

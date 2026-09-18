package fr.sylvainjanet.tracker.journal.application.port.in.dtos.result;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LogWeightMeasurementResult(LocalDate date, BigDecimal weightInKg) {}

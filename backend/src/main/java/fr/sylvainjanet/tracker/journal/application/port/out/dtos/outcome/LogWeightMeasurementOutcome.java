package fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LogWeightMeasurementOutcome(LocalDate date, BigDecimal weightInKg) {}

package fr.sylvainjanet.tracker.journal.application.port.in.dtos.result;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GetWeightMeasurementByDateResult(LocalDate date, BigDecimal weightInKg) {}

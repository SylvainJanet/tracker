package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnalysisValueResult(LocalDate date, long dayNumber, BigDecimal weightInKg) {}

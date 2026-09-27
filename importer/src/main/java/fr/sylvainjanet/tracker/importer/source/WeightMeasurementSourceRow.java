package fr.sylvainjanet.tracker.importer.source;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeightMeasurementSourceRow(
        long sourceRowNumber, LocalDate date, BigDecimal weightInKg) {}

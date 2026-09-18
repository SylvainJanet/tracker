package fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LogWeightMeasurementInstruction(LocalDate date, BigDecimal weightInKg) {}

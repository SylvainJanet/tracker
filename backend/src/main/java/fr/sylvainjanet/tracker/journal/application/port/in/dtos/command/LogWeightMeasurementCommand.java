package fr.sylvainjanet.tracker.journal.application.port.in.dtos.command;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LogWeightMeasurementCommand(LocalDate date, BigDecimal weightInKg) {}

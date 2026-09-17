package fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction;

import java.time.LocalDate;

public record LogWeightMeasurementInstruction(LocalDate date, Float weightInKg) {}

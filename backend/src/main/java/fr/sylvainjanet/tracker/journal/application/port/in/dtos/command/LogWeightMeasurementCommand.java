package fr.sylvainjanet.tracker.journal.application.port.in.dtos.command;

import java.time.LocalDate;

public record LogWeightMeasurementCommand(LocalDate date, Float weightInKg) {}

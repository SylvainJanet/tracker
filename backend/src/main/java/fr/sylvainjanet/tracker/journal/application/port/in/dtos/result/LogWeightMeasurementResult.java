package fr.sylvainjanet.tracker.journal.application.port.in.dtos.result;

import java.time.LocalDate;

public record LogWeightMeasurementResult(LocalDate date, Float weightInKg) {}

package fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome;

import java.time.LocalDate;

public record LogWeightMeasurementOutcome(LocalDate date, Float weightInKg) {}

package fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria;

import java.time.LocalDate;

public record GetWeightMeasurementByDateCriteria(LocalDate date) {}

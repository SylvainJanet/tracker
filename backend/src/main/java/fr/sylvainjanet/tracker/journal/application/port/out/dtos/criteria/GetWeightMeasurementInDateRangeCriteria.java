package fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria;

import java.time.LocalDate;

public record GetWeightMeasurementInDateRangeCriteria(LocalDate startDate, LocalDate endDate) {}

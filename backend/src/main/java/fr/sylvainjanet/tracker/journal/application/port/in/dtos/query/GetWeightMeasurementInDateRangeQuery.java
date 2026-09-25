package fr.sylvainjanet.tracker.journal.application.port.in.dtos.query;

import java.time.LocalDate;

public record GetWeightMeasurementInDateRangeQuery(LocalDate startDate, LocalDate endDate) {}

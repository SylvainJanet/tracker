package fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.response;

import java.time.LocalDate;

public record LogWeightMeasurementResponse(LocalDate date, Float weightInKg) {}

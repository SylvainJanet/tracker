package fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome;

import java.util.List;

public record GetWeightMeasurementInDateRangeOutcome(
        List<WeightMeasurementOutcome> weightMeasurementsByDate) {}

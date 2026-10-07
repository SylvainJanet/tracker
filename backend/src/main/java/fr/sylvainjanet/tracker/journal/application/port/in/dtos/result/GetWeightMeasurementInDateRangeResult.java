package fr.sylvainjanet.tracker.journal.application.port.in.dtos.result;

import java.util.List;

public record GetWeightMeasurementInDateRangeResult(
        List<WeightMeasurementResult> weightMeasurementsByDate) {}

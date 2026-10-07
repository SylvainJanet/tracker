package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result;

import java.util.Set;

public record ValueResult(ExactValueResult exactValue, Set<ApproximationResult> approximations) {}

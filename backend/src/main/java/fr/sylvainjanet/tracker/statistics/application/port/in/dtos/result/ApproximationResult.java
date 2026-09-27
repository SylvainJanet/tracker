package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result;

import java.math.BigDecimal;

public record ApproximationResult(BigDecimal value, CalculationRoundingResult rounding) {}

package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result;

import java.math.BigDecimal;

public record FractionResult(BigDecimal numerator, BigDecimal denominator)
        implements ExactValueResult {}

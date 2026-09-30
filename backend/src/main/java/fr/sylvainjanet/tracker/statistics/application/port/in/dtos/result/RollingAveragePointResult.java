package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result;

import java.util.Set;

public record RollingAveragePointResult(
        long index, Set<Long> includedIndexes, ValueResult rollingAverage) {}

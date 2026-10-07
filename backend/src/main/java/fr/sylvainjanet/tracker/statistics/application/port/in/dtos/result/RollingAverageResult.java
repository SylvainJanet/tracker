package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result;

import java.util.List;

public record RollingAverageResult(long windowSize, List<RollingAveragePointResult> points) {}

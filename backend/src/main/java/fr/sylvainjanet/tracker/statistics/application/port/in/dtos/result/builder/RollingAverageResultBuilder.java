package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.RollingAveragePointResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.RollingAverageResult;
import java.util.ArrayList;
import java.util.List;

public final class RollingAverageResultBuilder {

    private long windowSize;
    private final List<RollingAveragePointResult> points = new ArrayList<>();

    private RollingAverageResultBuilder() {}

    public static RollingAverageResultBuilder aRollingAverageResult() {
        return new RollingAverageResultBuilder();
    }

    public RollingAverageResultBuilder withWindowSize(long windowSize) {
        this.windowSize = windowSize;
        return this;
    }

    public RollingAverageResultBuilder withPoints(List<RollingAveragePointResult> points) {
        this.points.addAll(points);
        return this;
    }

    public RollingAverageResult build() {
        return new RollingAverageResult(windowSize, points);
    }
}

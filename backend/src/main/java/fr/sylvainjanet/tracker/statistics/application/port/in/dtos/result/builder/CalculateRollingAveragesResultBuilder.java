package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult.RollingAveragePointResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult.RollingAverageResult;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CalculateRollingAveragesResultBuilder {

    private final List<RollingAverageResult> rollingAverages = new ArrayList<>();

    private CalculateRollingAveragesResultBuilder() {}

    public static final class RollingAverageResultBuilder {

        private int windowSize;
        private final List<RollingAveragePointResult> points = new ArrayList<>();

        private RollingAverageResultBuilder() {}

        public static RollingAverageResultBuilder aRollingAverageResult() {
            return new RollingAverageResultBuilder();
        }

        public RollingAverageResultBuilder withWindowSize(int windowSize) {
            this.windowSize = windowSize;
            return this;
        }

        public RollingAverageResultBuilder withPoints(List<RollingAveragePointResult> points) {
            this.points.addAll(Objects.requireNonNull(points, "points must not be null"));
            return this;
        }

        public RollingAverageResult build() {
            return new RollingAverageResult(windowSize, points);
        }
    }

    public static final class RollingAveragePointResultBuilder {

        private long index;
        private BigDecimal average;

        private RollingAveragePointResultBuilder() {}

        public static RollingAveragePointResultBuilder aRollingAveragePointResult() {
            return new RollingAveragePointResultBuilder();
        }

        public RollingAveragePointResultBuilder withIndex(long index) {
            this.index = index;
            return this;
        }

        public RollingAveragePointResultBuilder withAverage(BigDecimal average) {
            this.average = average;
            return this;
        }

        public RollingAveragePointResult build() {
            return new RollingAveragePointResult(index, average);
        }
    }

    public static CalculateRollingAveragesResultBuilder aCalculateRollingAveragesResult() {
        return new CalculateRollingAveragesResultBuilder();
    }

    public CalculateRollingAveragesResultBuilder withRollingAverages(
            List<RollingAverageResult> rollingAverages) {
        this.rollingAverages.addAll(
                Objects.requireNonNull(rollingAverages, "rolling averages must not be null"));
        return this;
    }

    public CalculateRollingAveragesResult build() {
        return new CalculateRollingAveragesResult(rollingAverages);
    }
}

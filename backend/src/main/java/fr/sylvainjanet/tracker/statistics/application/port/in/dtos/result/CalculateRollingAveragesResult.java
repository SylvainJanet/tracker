package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record CalculateRollingAveragesResult(List<RollingAverageResult> rollingAverages) {

    public CalculateRollingAveragesResult {
        rollingAverages =
                List.copyOf(
                        Objects.requireNonNull(
                                rollingAverages, "rolling averages must not be null"));
    }

    public static CalculateRollingAveragesResult empty() {
        return new CalculateRollingAveragesResult(List.of());
    }

    public record RollingAverageResult(int windowSize, List<RollingAveragePointResult> points) {

        public RollingAverageResult {
            points = List.copyOf(Objects.requireNonNull(points, "points must not be null"));
        }
    }

    public record RollingAveragePointResult(
            long index, BigDecimal average, int includedValueCount) {

        public RollingAveragePointResult {
            Objects.requireNonNull(average, "average must not be null");
        }
    }
}

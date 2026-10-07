package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result;

import java.util.List;

public record CalculateRollingAveragesResult(List<RollingAverageResult> rollingAverages) {

    public static CalculateRollingAveragesResult empty() {
        return new CalculateRollingAveragesResult(List.of());
    }
}

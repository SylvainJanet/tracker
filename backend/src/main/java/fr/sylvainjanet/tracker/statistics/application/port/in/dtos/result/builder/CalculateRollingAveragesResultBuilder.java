package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.RollingAverageResult;
import java.util.ArrayList;
import java.util.List;

public final class CalculateRollingAveragesResultBuilder {

    private final List<RollingAverageResult> rollingAverages = new ArrayList<>();

    private CalculateRollingAveragesResultBuilder() {}

    public static CalculateRollingAveragesResultBuilder aCalculateRollingAveragesResult() {
        return new CalculateRollingAveragesResultBuilder();
    }

    public CalculateRollingAveragesResultBuilder withRollingAverages(
            List<RollingAverageResult> rollingAverages) {
        this.rollingAverages.addAll(rollingAverages);
        return this;
    }

    public CalculateRollingAveragesResult build() {
        return new CalculateRollingAveragesResult(rollingAverages);
    }
}

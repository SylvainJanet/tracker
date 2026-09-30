package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.RollingAveragePointResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ValueResult;
import java.util.HashSet;
import java.util.Set;

public final class RollingAveragePointResultBuilder {

    private long index;
    private final Set<Long> includedIndexes = new HashSet<>();
    private ValueResult rollingAverage;

    private RollingAveragePointResultBuilder() {}

    public static RollingAveragePointResultBuilder aRollingAveragePointResultBuilder() {
        return new RollingAveragePointResultBuilder();
    }

    public RollingAveragePointResultBuilder withIndex(long index) {
        this.index = index;
        return this;
    }

    public RollingAveragePointResultBuilder withIncludedIndexes(Set<Long> includedIndexes) {
        this.includedIndexes.addAll(includedIndexes);
        return this;
    }

    public RollingAveragePointResultBuilder withRollingAverage(ValueResult rollingAverage) {
        this.rollingAverage = rollingAverage;
        return this;
    }

    public RollingAveragePointResult build() {
        return new RollingAveragePointResult(index, includedIndexes, rollingAverage);
    }
}

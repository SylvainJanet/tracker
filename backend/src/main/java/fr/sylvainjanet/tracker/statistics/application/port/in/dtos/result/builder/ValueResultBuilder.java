package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ApproximationResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ExactValueResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ValueResult;
import java.util.HashSet;
import java.util.Set;

public final class ValueResultBuilder {

    private ExactValueResult exactValue;
    private final Set<ApproximationResult> approximations = new HashSet<>();

    private ValueResultBuilder() {}

    public static ValueResultBuilder aValueResult() {
        return new ValueResultBuilder();
    }

    public ValueResultBuilder withExactValue(ExactValueResult exactValue) {
        this.exactValue = exactValue;
        return this;
    }

    public ValueResultBuilder withApproximations(Set<ApproximationResult> approximations) {
        this.approximations.addAll(approximations);
        return this;
    }

    public ValueResult build() {
        return new ValueResult(exactValue, approximations);
    }
}

package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAveragePointResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageValueResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisValueResult;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public final class AnalysisRollingAveragePointResultBuilder {

    private LocalDate date;
    private long dayNumber;
    private final Set<AnalysisValueResult> includedValues = new HashSet<>();
    private AnalysisRollingAverageValueResult rollingAverage;

    private AnalysisRollingAveragePointResultBuilder() {}

    public static AnalysisRollingAveragePointResultBuilder anAnalysisRollingAveragePointResult() {
        return new AnalysisRollingAveragePointResultBuilder();
    }

    public AnalysisRollingAveragePointResultBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public AnalysisRollingAveragePointResultBuilder withDayNumber(long dayNumber) {
        this.dayNumber = dayNumber;
        return this;
    }

    public AnalysisRollingAveragePointResultBuilder withIncludedValues(
            Set<AnalysisValueResult> includedValues) {
        this.includedValues.addAll(includedValues);
        return this;
    }

    public AnalysisRollingAveragePointResultBuilder withRollingAverage(
            AnalysisRollingAverageValueResult rollingAverage) {
        this.rollingAverage = rollingAverage;
        return this;
    }

    public AnalysisRollingAveragePointResult build() {
        return new AnalysisRollingAveragePointResult(
                date, dayNumber, includedValues, rollingAverage);
    }
}

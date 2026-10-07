package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRollingAveragePointResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRollingAverageValueResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisValueResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class AnalysisRollingAveragePointResponseBuilder {

    private LocalDate date;
    private long dayNumber;
    private final List<AnalysisValueResponse> includedValues = new ArrayList<>();
    private AnalysisRollingAverageValueResponse rollingAverage;

    private AnalysisRollingAveragePointResponseBuilder() {}

    public static AnalysisRollingAveragePointResponseBuilder
            anAnalysisRollingAveragePointResponse() {
        return new AnalysisRollingAveragePointResponseBuilder();
    }

    public AnalysisRollingAveragePointResponseBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public AnalysisRollingAveragePointResponseBuilder withDayNumber(long dayNumber) {
        this.dayNumber = dayNumber;
        return this;
    }

    public AnalysisRollingAveragePointResponseBuilder withIncludedValues(
            List<AnalysisValueResponse> includedValues) {
        this.includedValues.addAll(includedValues);
        return this;
    }

    public AnalysisRollingAveragePointResponseBuilder withRollingAverage(
            AnalysisRollingAverageValueResponse rollingAverage) {
        this.rollingAverage = rollingAverage;
        return this;
    }

    public AnalysisRollingAveragePointResponse build() {
        return new AnalysisRollingAveragePointResponse(
                date, dayNumber, includedValues, rollingAverage);
    }
}

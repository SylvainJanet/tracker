package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisDateRangeResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisValueResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GetWeightAnalysisResultBuilder {

    private LocalDate timelineStartDate;
    private AnalysisDateRangeResult dateRange;
    private final List<AnalysisValueResult> weightValues = new ArrayList<>();
    private final List<AnalysisRollingAverageResult> rollingAverageSeries = new ArrayList<>();

    private GetWeightAnalysisResultBuilder() {}

    public static GetWeightAnalysisResultBuilder aGetWeightAnalysisResult() {
        return new GetWeightAnalysisResultBuilder();
    }

    public GetWeightAnalysisResultBuilder withTimelineStartDate(LocalDate timelineStartDate) {
        this.timelineStartDate = timelineStartDate;
        return this;
    }

    public GetWeightAnalysisResultBuilder withDateRange(AnalysisDateRangeResult dateRange) {
        this.dateRange = dateRange;
        return this;
    }

    public GetWeightAnalysisResultBuilder withWeightValues(List<AnalysisValueResult> weightValues) {
        this.weightValues.addAll(
                Objects.requireNonNull(weightValues, "weight values must not be null"));
        return this;
    }

    public GetWeightAnalysisResultBuilder withRollingAverageSeries(
            List<AnalysisRollingAverageResult> rollingAverageSeries) {
        this.rollingAverageSeries.addAll(
                Objects.requireNonNull(
                        rollingAverageSeries, "rolling average series must not be null"));
        return this;
    }

    public GetWeightAnalysisResult build() {
        return new GetWeightAnalysisResult(
                timelineStartDate, dateRange, weightValues, rollingAverageSeries);
    }
}

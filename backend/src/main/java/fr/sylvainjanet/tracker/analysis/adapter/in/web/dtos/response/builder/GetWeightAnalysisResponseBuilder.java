package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisDateRangeResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRollingAverageResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisValueResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class GetWeightAnalysisResponseBuilder {

    private LocalDate timelineStartDate;
    private AnalysisDateRangeResponse range;
    private final List<AnalysisValueResponse> weightMeasurements = new ArrayList<>();
    private final List<AnalysisRollingAverageResponse> rollingAverageSeries = new ArrayList<>();

    private GetWeightAnalysisResponseBuilder() {}

    public static GetWeightAnalysisResponseBuilder aGetWeightAnalysisResponse() {
        return new GetWeightAnalysisResponseBuilder();
    }

    public GetWeightAnalysisResponseBuilder withTimelineStartDate(LocalDate timelineStartDate) {
        this.timelineStartDate = timelineStartDate;
        return this;
    }

    public GetWeightAnalysisResponseBuilder withRange(AnalysisDateRangeResponse range) {
        this.range = range;
        return this;
    }

    public GetWeightAnalysisResponseBuilder withWeightMeasurements(
            List<AnalysisValueResponse> weightMeasurements) {
        this.weightMeasurements.addAll(weightMeasurements);
        return this;
    }

    public GetWeightAnalysisResponseBuilder withRollingAverageSeries(
            List<AnalysisRollingAverageResponse> rollingAverageSeries) {
        this.rollingAverageSeries.addAll(rollingAverageSeries);
        return this;
    }

    public GetWeightAnalysisResponse build() {
        return new GetWeightAnalysisResponse(
                timelineStartDate, range, weightMeasurements, rollingAverageSeries);
    }
}

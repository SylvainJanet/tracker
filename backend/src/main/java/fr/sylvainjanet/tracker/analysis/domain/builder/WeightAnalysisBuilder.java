package fr.sylvainjanet.tracker.analysis.domain.builder;

import fr.sylvainjanet.tracker.analysis.domain.WeightAnalysis;
import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.shared.domain.WeightMeasurement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeightAnalysisBuilder {

    private LocalDate timelineStartDate;
    private DateRange range;
    private final List<WeightMeasurement> weightMeasurements = new ArrayList<>();

    private WeightAnalysisBuilder() {}

    public static WeightAnalysisBuilder aWeightAnalysis() {
        return new WeightAnalysisBuilder();
    }

    public WeightAnalysisBuilder withTimelineStartDate(LocalDate date) {
        this.timelineStartDate = date;
        return this;
    }

    public WeightAnalysisBuilder withRange(DateRange range) {
        this.range = range;
        return this;
    }

    public WeightAnalysisBuilder withWeightMeasurements(
            List<WeightMeasurement> weightMeasurements) {
        this.weightMeasurements.addAll(weightMeasurements);
        return this;
    }

    public WeightAnalysis build() {
        return WeightAnalysis.create(timelineStartDate, range, weightMeasurements);
    }
}

package fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.WeightMeasurementResult;
import java.util.ArrayList;
import java.util.List;

public final class GetWeightMeasurementInDateRangeResultBuilder {

    private final List<WeightMeasurementResult> weightMeasurementsByDate = new ArrayList<>();

    private GetWeightMeasurementInDateRangeResultBuilder() {}

    public static GetWeightMeasurementInDateRangeResultBuilder
            aGetWeightMeasurementInDateRangeResult() {
        return new GetWeightMeasurementInDateRangeResultBuilder();
    }

    public GetWeightMeasurementInDateRangeResultBuilder withWeightMeasurementsByDate(
            List<WeightMeasurementResult> weightMeasurementsByDate) {
        this.weightMeasurementsByDate.addAll(weightMeasurementsByDate);
        return this;
    }

    public GetWeightMeasurementInDateRangeResult build() {
        return new GetWeightMeasurementInDateRangeResult(weightMeasurementsByDate);
    }
}

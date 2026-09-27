package fr.sylvainjanet.tracker.analysis.application.service.mapper.result;

import fr.sylvainjanet.tracker.analysis.domain.value.DatedValue;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import java.util.List;

public final class GetWeightMeasurementInDateRangeResultMapper {

    private GetWeightMeasurementInDateRangeResultMapper() {}

    public static List<DatedValue> loadValues(GetWeightMeasurementInDateRangeResult result) {
        return result.weightMeasurementsByDate().stream()
                .map(measurement -> DatedValue.create(measurement.date(), measurement.weightInKg()))
                .toList();
    }
}

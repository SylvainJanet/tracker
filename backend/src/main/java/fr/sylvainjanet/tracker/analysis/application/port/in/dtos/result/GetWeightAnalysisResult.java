package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result;

import java.time.LocalDate;
import java.util.List;

public record GetWeightAnalysisResult(
        LocalDate timelineStartDate,
        AnalysisDateRangeResult dateRange,
        List<AnalysisValueResult> weightValues,
        List<AnalysisRollingAverageResult> rollingAverageSeries) {

    public static GetWeightAnalysisResult noMeasurements() {
        return new GetWeightAnalysisResult(null, null, List.of(), List.of());
    }
}

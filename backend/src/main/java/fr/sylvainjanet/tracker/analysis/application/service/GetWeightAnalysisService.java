package fr.sylvainjanet.tracker.analysis.application.service;

import static fr.sylvainjanet.tracker.analysis.application.service.mapper.GetWeightAnalysisServiceMapper.analysisToResult;
import static fr.sylvainjanet.tracker.analysis.application.service.mapper.GetWeightAnalysisServiceMapper.dateRangeToQuery;
import static fr.sylvainjanet.tracker.analysis.application.service.mapper.GetWeightAnalysisServiceMapper.measurementsToDomain;
import static fr.sylvainjanet.tracker.analysis.domain.builder.WeightAnalysisBuilder.aWeightAnalysis;
import static fr.sylvainjanet.tracker.shared.domain.builder.DateRangeBuilder.aDateRange;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.usecase.GetWeightAnalysisUseCase;
import fr.sylvainjanet.tracker.analysis.domain.WeightAnalysis;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetFirstWeightMeasurementDateResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetFirstWeightMeasurementDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementInDateRangeUseCase;
import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.shared.domain.WeightMeasurement;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class GetWeightAnalysisService implements GetWeightAnalysisUseCase {

    private final GetFirstWeightMeasurementDateUseCase getFirstWeightMeasurementDate;
    private final GetWeightMeasurementInDateRangeUseCase getWeightMeasurementsInRange;
    private final Clock clock;

    public GetWeightAnalysisService(
            GetFirstWeightMeasurementDateUseCase getFirstWeightMeasurementDate,
            GetWeightMeasurementInDateRangeUseCase getWeightMeasurementsInRange,
            Clock clock) {
        this.getFirstWeightMeasurementDate =
                Objects.requireNonNull(
                        getFirstWeightMeasurementDate,
                        "get first weight measurement date must not be null");
        this.getWeightMeasurementsInRange =
                Objects.requireNonNull(
                        getWeightMeasurementsInRange,
                        "get weight measurements in range must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public GetWeightAnalysisResult get() {
        LocalDate today = LocalDate.now(clock);
        Optional<LocalDate> timelineStartDate = findTimelineStartDate();

        if (timelineStartDate.isEmpty() || timelineStartDate.get().isAfter(today)) {
            return GetWeightAnalysisResult.empty();
        }

        return analyze(timelineStartDate.get(), today);
    }

    private Optional<LocalDate> findTimelineStartDate() {
        return getFirstWeightMeasurementDate.get().map(GetFirstWeightMeasurementDateResult::date);
    }

    private GetWeightAnalysisResult analyze(LocalDate timelineStartDate, LocalDate today) {
        DateRange range = aDateRange().withStartDate(timelineStartDate).withEndDate(today).build();
        List<WeightMeasurement> measurements = loadMeasurements(range);

        if (measurements.isEmpty()) {
            return GetWeightAnalysisResult.empty();
        }

        WeightAnalysis analysis =
                aWeightAnalysis()
                        .withTimelineStartDate(timelineStartDate)
                        .withRange(range)
                        .withWeightMeasurements(measurements)
                        .build();

        return analysisToResult(analysis);
    }

    private List<WeightMeasurement> loadMeasurements(DateRange range) {
        GetWeightMeasurementInDateRangeQuery query = dateRangeToQuery(range);
        GetWeightMeasurementInDateRangeResult result = getWeightMeasurementsInRange.get(query);

        return measurementsToDomain(result);
    }
}

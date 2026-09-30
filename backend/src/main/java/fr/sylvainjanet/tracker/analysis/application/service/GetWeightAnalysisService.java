package fr.sylvainjanet.tracker.analysis.application.service;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.usecase.GetWeightAnalysisUseCase;
import fr.sylvainjanet.tracker.analysis.application.service.mapper.command.CalculateRollingAveragesCommandMapper;
import fr.sylvainjanet.tracker.analysis.application.service.mapper.query.GetWeightMeasurementInDateRangeQueryMapper;
import fr.sylvainjanet.tracker.analysis.application.service.mapper.result.GetFirstWeightMeasurementDateResultMapper;
import fr.sylvainjanet.tracker.analysis.application.service.mapper.result.GetWeightAnalysisResultMapper;
import fr.sylvainjanet.tracker.analysis.application.service.mapper.result.GetWeightMeasurementInDateRangeResultMapper;
import fr.sylvainjanet.tracker.analysis.domain.value.DatedSeries;
import fr.sylvainjanet.tracker.analysis.domain.value.DatedValue;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetFirstWeightMeasurementDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementInDateRangeUseCase;
import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.usecase.CalculateRollingAveragesUseCase;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class GetWeightAnalysisService implements GetWeightAnalysisUseCase {

    private static final List<Long> ROLLING_WINDOWS_IN_DAYS =
            List.of(7L, 14L, 28L, 60L, 180L, 360L);

    private final GetFirstWeightMeasurementDateUseCase getFirstWeightMeasurementDate;
    private final GetWeightMeasurementInDateRangeUseCase getWeightMeasurementsInRange;
    private final CalculateRollingAveragesUseCase calculateRollingAverages;
    private final Clock clock;

    public GetWeightAnalysisService(
            GetFirstWeightMeasurementDateUseCase getFirstWeightMeasurementDate,
            GetWeightMeasurementInDateRangeUseCase getWeightMeasurementsInRange,
            CalculateRollingAveragesUseCase calculateRollingAverages,
            Clock clock) {
        this.getFirstWeightMeasurementDate =
                Objects.requireNonNull(
                        getFirstWeightMeasurementDate,
                        "get first weight measurement date must not be null");
        this.getWeightMeasurementsInRange =
                Objects.requireNonNull(
                        getWeightMeasurementsInRange,
                        "get weight measurements in dateRange must not be null");
        this.calculateRollingAverages =
                Objects.requireNonNull(
                        calculateRollingAverages, "calculate rolling averages must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public GetWeightAnalysisResult get() {
        LocalDate today = LocalDate.now(clock);
        Optional<LocalDate> timelineStartDate = findTimelineStartDate();

        if (timelineStartDate.isEmpty() || timelineStartDate.get().isAfter(today)) {
            return GetWeightAnalysisResult.noMeasurements();
        }

        DateRange analysisDateRange = DateRange.create(timelineStartDate.get(), today);
        return analyzeWeightInDateRange(analysisDateRange);
    }

    private Optional<LocalDate> findTimelineStartDate() {
        return getFirstWeightMeasurementDate
                .get()
                .map(GetFirstWeightMeasurementDateResultMapper::localDate);
    }

    private GetWeightAnalysisResult analyzeWeightInDateRange(DateRange range) {
        List<DatedValue> values = loadValues(range);

        if (values.isEmpty()) {
            return GetWeightAnalysisResult.noMeasurements();
        }

        DatedSeries series = DatedSeries.completeSeries(range, values);

        CalculateRollingAveragesResult statisticsResult =
                calculateRollingAverages.calculate(
                        CalculateRollingAveragesCommandMapper.calculateCommand(
                                series, ROLLING_WINDOWS_IN_DAYS));
        return GetWeightAnalysisResultMapper.analysisResult(series, statisticsResult);
    }

    private List<DatedValue> loadValues(DateRange range) {
        GetWeightMeasurementInDateRangeQuery query =
                GetWeightMeasurementInDateRangeQueryMapper.query(range);

        return GetWeightMeasurementInDateRangeResultMapper.loadValues(
                getWeightMeasurementsInRange.get(query));
    }
}

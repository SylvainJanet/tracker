package fr.sylvainjanet.tracker.journal.application.service;

import static fr.sylvainjanet.tracker.journal.domain.builder.WeightMeasurementBuilder.aWeightMeasurement;
import static fr.sylvainjanet.tracker.shared.domain.builder.DateRangeBuilder.aDateRange;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult.WeightMeasurementByDateResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementInDateRangeUseCase;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementInDateRangeCriteria;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementInDateRangeOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementInDateRangeOutcome.WeightMeasurementByDateOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import fr.sylvainjanet.tracker.journal.domain.WeightMeasurement;
import fr.sylvainjanet.tracker.shared.domain.DateRange;
import java.util.List;
import java.util.Objects;

public final class GetWeightMeasurementInDateRangeService
        implements GetWeightMeasurementInDateRangeUseCase {

    private final WeightMeasurementStore store;

    public GetWeightMeasurementInDateRangeService(WeightMeasurementStore store) {
        this.store = Objects.requireNonNull(store, "store must not be null");
    }

    @Override
    public GetWeightMeasurementInDateRangeResult get(GetWeightMeasurementInDateRangeQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        DateRange dateRange = queryToDomain(query);

        GetWeightMeasurementInDateRangeOutcome outcome =
                store.getInDateRange(
                        new GetWeightMeasurementInDateRangeCriteria(
                                dateRange.getStartDate(), dateRange.getEndDate()));

        List<WeightMeasurementByDateResult> results =
                outcome.weightMeasurementsByDate().stream()
                        .map(this::outcomeToDomain)
                        .map(this::toResult)
                        .toList();

        return new GetWeightMeasurementInDateRangeResult(results);
    }

    private DateRange queryToDomain(GetWeightMeasurementInDateRangeQuery query) {
        return aDateRange().withStartDate(query.startDate()).withEndDate(query.endDate()).build();
    }

    private WeightMeasurement outcomeToDomain(WeightMeasurementByDateOutcome outcome) {
        return aWeightMeasurement()
                .withDate(outcome.date())
                .withWeightInKg(outcome.weightInKg())
                .build();
    }

    private WeightMeasurementByDateResult toResult(WeightMeasurement measurement) {
        return new WeightMeasurementByDateResult(
                measurement.date(), measurement.weightInKilograms());
    }
}

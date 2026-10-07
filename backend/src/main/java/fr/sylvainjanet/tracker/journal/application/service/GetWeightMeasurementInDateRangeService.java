package fr.sylvainjanet.tracker.journal.application.service;

import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.builder.GetWeightMeasurementInDateRangeResultBuilder.aGetWeightMeasurementInDateRangeResult;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementInDateRangeUseCase;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import fr.sylvainjanet.tracker.journal.application.service.mapper.criteria.GetWeightMeasurementInDateRangeCriteriaMapper;
import fr.sylvainjanet.tracker.journal.application.service.mapper.outcome.WeightMeasurementOutcomeMapper;
import fr.sylvainjanet.tracker.journal.application.service.mapper.query.GetWeightMeasurementInDateRangeQueryMapper;
import fr.sylvainjanet.tracker.journal.application.service.mapper.result.WeightMeasurementResultMapper;
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

        return aGetWeightMeasurementInDateRangeResult()
                .withWeightMeasurementsByDate(
                        store
                                .getInDateRange(
                                        GetWeightMeasurementInDateRangeCriteriaMapper.criteria(
                                                GetWeightMeasurementInDateRangeQueryMapper
                                                        .dateRange(query)))
                                .weightMeasurementsByDate()
                                .stream()
                                .map(WeightMeasurementOutcomeMapper::measurement)
                                .map(WeightMeasurementResultMapper::result)
                                .toList())
                .build();
    }
}

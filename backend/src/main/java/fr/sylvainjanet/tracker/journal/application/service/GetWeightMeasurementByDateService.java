package fr.sylvainjanet.tracker.journal.application.service;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementByDateQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.WeightMeasurementResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementByDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import fr.sylvainjanet.tracker.journal.application.service.mapper.criteria.GetWeightMeasurementByDateCriteriaMapper;
import fr.sylvainjanet.tracker.journal.application.service.mapper.outcome.WeightMeasurementOutcomeMapper;
import fr.sylvainjanet.tracker.journal.application.service.mapper.query.GetWeightMeasurementByDateQueryMapper;
import fr.sylvainjanet.tracker.journal.application.service.mapper.result.WeightMeasurementResultMapper;
import java.util.Objects;
import java.util.Optional;

public final class GetWeightMeasurementByDateService implements GetWeightMeasurementByDateUseCase {

    private final WeightMeasurementStore store;

    public GetWeightMeasurementByDateService(WeightMeasurementStore store) {
        this.store = Objects.requireNonNull(store, "store must not be null");
    }

    @Override
    public Optional<WeightMeasurementResult> get(GetWeightMeasurementByDateQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return store.getByDate(
                        GetWeightMeasurementByDateCriteriaMapper.dateCriteria(
                                GetWeightMeasurementByDateQueryMapper.localDate(query)))
                .map(WeightMeasurementOutcomeMapper::measurement)
                .map(WeightMeasurementResultMapper::result);
    }
}

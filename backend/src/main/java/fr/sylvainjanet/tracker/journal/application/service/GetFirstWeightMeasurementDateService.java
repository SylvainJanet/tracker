package fr.sylvainjanet.tracker.journal.application.service;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetFirstWeightMeasurementDateResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetFirstWeightMeasurementDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import fr.sylvainjanet.tracker.journal.application.service.mapper.outcome.GetFirstWeightMeasurementDateOutcomeMapper;
import fr.sylvainjanet.tracker.journal.application.service.mapper.result.GetFirstWeightMeasurementDateResultMapper;
import java.util.Objects;
import java.util.Optional;

public final class GetFirstWeightMeasurementDateService
        implements GetFirstWeightMeasurementDateUseCase {

    private final WeightMeasurementStore store;

    public GetFirstWeightMeasurementDateService(WeightMeasurementStore store) {
        this.store = Objects.requireNonNull(store, "store must not be null");
    }

    @Override
    public Optional<GetFirstWeightMeasurementDateResult> get() {
        return store.getFirstWeightMeasurementDate()
                .map(GetFirstWeightMeasurementDateOutcomeMapper::localDate)
                .map(GetFirstWeightMeasurementDateResultMapper::result);
    }
}

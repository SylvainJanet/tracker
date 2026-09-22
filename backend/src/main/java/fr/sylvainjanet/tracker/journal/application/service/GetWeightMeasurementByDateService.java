package fr.sylvainjanet.tracker.journal.application.service;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementByDateQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementByDateResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementByDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementByDateCriteria;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementByDateOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import fr.sylvainjanet.tracker.journal.domain.Weight;
import fr.sylvainjanet.tracker.journal.domain.WeightMeasurement;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public final class GetWeightMeasurementByDateService implements GetWeightMeasurementByDateUseCase {

    private final WeightMeasurementStore store;

    public GetWeightMeasurementByDateService(WeightMeasurementStore store) {
        this.store = Objects.requireNonNull(store, "store must not be null");
    }

    @Override
    public Optional<GetWeightMeasurementByDateResult> get(GetWeightMeasurementByDateQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        LocalDate date = queryToDomain(query);

        Optional<GetWeightMeasurementByDateOutcome> outcomeOptional =
                store.getByDate(toInstruction(date));

        Optional<WeightMeasurement> domainOptional = outcomeOptional.map(this::outcomeToDomain);

        return domainOptional.map(this::toResult);
    }

    private GetWeightMeasurementByDateResult toResult(WeightMeasurement measurement) {
        return new GetWeightMeasurementByDateResult(
                measurement.date(), measurement.weightInKilograms());
    }

    private GetWeightMeasurementByDateCriteria toInstruction(LocalDate date) {
        return new GetWeightMeasurementByDateCriteria(date);
    }

    private LocalDate queryToDomain(GetWeightMeasurementByDateQuery query) {
        return query.date();
    }

    private WeightMeasurement outcomeToDomain(GetWeightMeasurementByDateOutcome outcome) {
        return WeightMeasurement.create(outcome.date(), Weight.of(outcome.weightInKg()));
    }
}

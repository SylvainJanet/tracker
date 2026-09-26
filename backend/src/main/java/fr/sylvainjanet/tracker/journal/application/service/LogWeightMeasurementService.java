package fr.sylvainjanet.tracker.journal.application.service;

import static fr.sylvainjanet.tracker.journal.domain.builder.WeightMeasurementBuilder.aWeightMeasurement;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.LogWeightMeasurementResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.LogWeightMeasurementUseCase;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.LogWeightMeasurementOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import fr.sylvainjanet.tracker.journal.domain.WeightMeasurement;
import java.util.Objects;

public final class LogWeightMeasurementService implements LogWeightMeasurementUseCase {

    private final WeightMeasurementStore store;

    public LogWeightMeasurementService(WeightMeasurementStore store) {
        this.store = Objects.requireNonNull(store, "store must not be null");
    }

    @Override
    public LogWeightMeasurementResult log(LogWeightMeasurementCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        WeightMeasurement domainCommand = commandToDomain(command);

        LogWeightMeasurementOutcome outcome = store.log(toInstruction(domainCommand));

        WeightMeasurement domainOutcome = outcomeToDomain(outcome);

        return toResult(domainOutcome);
    }

    private WeightMeasurement commandToDomain(LogWeightMeasurementCommand command) {
        return aWeightMeasurement()
                .withDate(command.date())
                .withWeightInKg(command.weightInKg())
                .build();
    }

    private LogWeightMeasurementInstruction toInstruction(WeightMeasurement weightMeasurement) {
        return new LogWeightMeasurementInstruction(
                weightMeasurement.date(), weightMeasurement.weightInKilograms());
    }

    private LogWeightMeasurementResult toResult(WeightMeasurement weightMeasurement) {
        return new LogWeightMeasurementResult(
                weightMeasurement.date(), weightMeasurement.weightInKilograms());
    }

    private WeightMeasurement outcomeToDomain(LogWeightMeasurementOutcome outcome) {
        return aWeightMeasurement()
                .withDate(outcome.date())
                .withWeightInKg(outcome.weightInKg())
                .build();
    }
}

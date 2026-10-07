package fr.sylvainjanet.tracker.journal.application.service;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.WeightMeasurementResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.LogWeightMeasurementUseCase;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import fr.sylvainjanet.tracker.journal.application.service.mapper.command.LogWeightMeasurementCommandMapper;
import fr.sylvainjanet.tracker.journal.application.service.mapper.instruction.LogWeightMeasurementInstructionMapper;
import fr.sylvainjanet.tracker.journal.application.service.mapper.outcome.WeightMeasurementOutcomeMapper;
import fr.sylvainjanet.tracker.journal.application.service.mapper.result.WeightMeasurementResultMapper;
import java.util.Objects;

public final class LogWeightMeasurementService implements LogWeightMeasurementUseCase {

    private final WeightMeasurementStore store;

    public LogWeightMeasurementService(WeightMeasurementStore store) {
        this.store = Objects.requireNonNull(store, "store must not be null");
    }

    @Override
    public WeightMeasurementResult log(LogWeightMeasurementCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        return WeightMeasurementResultMapper.result(
                WeightMeasurementOutcomeMapper.measurement(
                        store.log(
                                LogWeightMeasurementInstructionMapper.logInstruction(
                                        LogWeightMeasurementCommandMapper.measurement(command)))));
    }
}
